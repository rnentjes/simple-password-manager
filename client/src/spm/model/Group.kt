package spm.model

import spm.ws.Tokenizer

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

    constructor(tk: Tokenizer) : this(tk.next().toLong(), tk.next(), tk.next() == "true", null) {
        if (id > lastId) {
            lastId = id
        }
        //console.log("Read group $name", this)
        val numberOfPasswords = tk.next().toInt()
        //println("\t Number of passwords :$numberOfPasswords")
        for (index in 0 until numberOfPasswords) {
            val password = Password(tk, this)

            passwords.add(password)
        }

        val numberOfChildren = tk.next().toInt()

        //println("\t Number of children :$numberOfChildren")
        // weird for-loop bug work-around
        var index = 0
        while (index < numberOfChildren) {
            //println("\t Child $index")
            val child = Group(tk)

            child.parent = this
            children.add(child)
            index++
        }
    }

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

    fun getPasswordsCountInGroup(): Int {
        var result = passwords.size

        for (child in children) {
            result += child.getPasswordsCountInGroup()
        }

        return result
    }

    fun getGroups(prefix: String = ""): List<Pair<String, String>> {
        val result = ArrayList<Pair<String, String>>()

        val childPrefix = if (prefix.isBlank()) {
            name
        } else {
            "$prefix / $name"
        }

        result.add("$id" to childPrefix)

        for (child in children) {
            result.addAll(child.getGroups(childPrefix))
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