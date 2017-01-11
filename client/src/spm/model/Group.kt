package spm.model

import spm.ws.Tokenizer
import java.util.*

/**
 * User: rnentjes
 * Date: 11-1-17
 * Time: 11:28
 */


data class Group(
  var id: Long,
  var name: String,
  var opened: Boolean = false,
  var parent: Group?,
  var found: Boolean = false,
  val children: MutableList<Group> = ArrayList(),
  val passwords: MutableList<Password> = ArrayList()
) {
    constructor(name: String, parent: Group) : this(nextId(), name, false, parent)

    constructor(tk: Tokenizer) : this(parseInt(tk.next()).toLong(), tk.next(), tk.next() == "true", null) {
        if (id > lastId) { lastId = id }
        val numberOfPasswords = parseInt(tk.next())
        for (index in 0..numberOfPasswords - 1) {
            val password = Password(tk, this)

            passwords.add(password)
        }

        val numberOfChildren = parseInt(tk.next())

        for (index in 0..numberOfChildren - 1) {
            val child = Group(tk)

            child.parent = this
            children.add(child)
        }
    }

    override fun equals(other: Any?) = super.equals(other)

    override fun hashCode() = super.hashCode()

    fun all(): List<Group> {
        val result = ArrayList<Group>()

        result.add(this)

        for (child in children) {
            result.addAll(child.all())
        }

        return result
    }

    fun export(): String {
        val result = StringBuilder()
        val tk = Tokenizer()

        result.append(tk.tokenize("$id", name, "$opened"))

        result.append(tk.seperator)
        result.append(passwords.size)
        for (password in passwords) {
            result.append(tk.seperator)
            result.append(password.tokenized())
        }

        result.append(tk.seperator)
        result.append(children.size)
        for (child in children) {
            result.append(tk.seperator)
            result.append(child.export())
        }

        return result.toString()
    }

    fun findById(id: Long): Group? {
        if (this.id == id) {
            return this
        } else {
            for (child in children) {
                val found = child.findById(id)

                if (found != null) {
                    return found
                }
            }
        }

        return null
    }

    fun search(value: String): Group? {
        var result: Group? = null

        if (name.toLowerCase().contains(value.toLowerCase())) {
            opened = false
            found = true

            result = this
            var parent = parent

            while (parent != null) {
                parent.opened = true
                parent = parent.parent
            }
        } else {
            opened = false
            found = false
        }

        if (opened) {
            var parent = parent

            while (parent != null) {
                parent.opened = true
                parent = parent.parent
            }
        }

        for (child in children) {
            val firstFound = child.search(value)

            if (result == null) {
                result = firstFound
            }
        }

        return result
    }

    fun getPasswordsCountInGroup(): Int {
        var result = passwords.size

        for (child in children) {
            result += child.getPasswordsCountInGroup()
        }

        return result
    }

    companion object {
        private var lastId = 0L

        fun nextId(): Long {
            return ++lastId
        }
    }
}