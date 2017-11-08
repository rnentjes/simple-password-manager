package spm.model

import spm.state.UserState
import spm.util.formatted
import spm.ws.Tokenizer
import kotlin.js.Date

/**
 * User: rnentjes
 * Date: 11-1-17
 * Time: 11:28
 */

data class HistoryEntry(
  var encryptedPassword: String,
  var from: String,
  var until: String
) {

    constructor(tk: Tokenizer): this(tk.next(), tk.next(), tk.next())

    fun tokenized(): String = Tokenizer.tokenize(
      encryptedPassword,
      from,
      until
    )
}

data class Password(
  var id: Long,
  var user: String,
  var group: Group,
  var title: String,
  var website: String,
  var username: String,
  var encryptedPassword: String,
  var password1: String = "",
  var password2: String = "",
  var description: String,
  var created: String,
  var history: MutableList<HistoryEntry> = ArrayList()
) {
    constructor(group: Group) : this(nextId(), "", group, "", "", "", "", "", "", "", Date().formatted())

    constructor(other: Password) : this(
      other.id,
      other.user,
      other.group,
      other.title,
      other.website,
      other.username,
      other.encryptedPassword,
      other.password1,
      other.password2,
      other.description,
      Date().formatted())

    constructor(tk: Tokenizer, group: Group): this(-1, "", group, "", "", "", "", "", "", "", Date().formatted()) {
        val first = tk.next()

        when (first) {
            "V3" -> {
                id = tk.next().toLong()
                user = tk.next()
                title = tk.next()
                website = tk.next()
                username = tk.next()
                encryptedPassword = tk.next()
                description = tk.next()
                created = tk.next()

                val historyData = Tokenizer(tk.next())

                while(!historyData.done()) {
                    history.add(HistoryEntry(Tokenizer(historyData.next())))
                }
            }
            "V2" -> {
                id = tk.next().toLong()
                user = tk.next()
                title = tk.next()
                website = tk.next()
                username = tk.next()
                encryptedPassword = tk.next()
                description = tk.next()

                val historyData = Tokenizer(tk.next())

                while(!historyData.done()) {
                    history.add(HistoryEntry(Tokenizer(historyData.next())))
                }
            }
            else -> {
                id = first.toLong()
                user = tk.next()
                title = tk.next()
                website = tk.next()
                username = tk.next()
                encryptedPassword = tk.next()
                description = tk.next()
            }
        }

    }

    fun tokenized(): String {
        val tk = Tokenizer()
        val tkHist = StringBuilder()

        for(index in 0 until this.history.size) {
            if (index > 0) {
                tkHist.append(tk.seperator)
            }
            tkHist.append(Tokenizer.tokenize(this.history[index].tokenized()))
        }

        return Tokenizer.tokenize(
          "V3",
          "$id",
          user,
          title,
          website,
          username,
          encryptedPassword,
          description,
          created,
          tkHist.toString()
        )
    }

    fun decrypt() {
        password1 = UserState.decryptPassword(encryptedPassword)
        password2 = password1
    }

    fun delete() {
        group.passwords.remove(this)
    }

    companion object {
        private var lastId = 0L

        fun nextId(): Long = ++lastId
    }

    fun search(value: String): Boolean {
        return username.toLowerCase().contains(value) ||
          title.toLowerCase().contains(value) ||
          website.toLowerCase().contains(value) ||
          description.toLowerCase().contains(value)
    }

    fun archivePassword() {
        history.add(HistoryEntry(
          encryptedPassword, created, Date().formatted()
        ))

        while(history.size > 3) {
            history.removeAt(0)
        }
    }

    fun hasHistory() = history.isNotEmpty()
}
