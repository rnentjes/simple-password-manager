package spm.model

import spm.state.UserState
import spm.ws.Tokenizer

/**
 * User: rnentjes
 * Date: 11-1-17
 * Time: 11:28
 */

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
  var description: String
) {
    constructor(group: Group) : this(nextId(), "", group, "", "", "", "", "", "", "")

    constructor(tk: Tokenizer, group: Group) : this(
      tk.next().toLong(),
      tk.next(),
      group,
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next(),
      "",
      "",
      tk.next()) {
        if (id > lastId) { lastId = id }
    }

    fun tokenized(): String = Tokenizer.tokenize("$id", user, title, website, username, encryptedPassword, description)

    fun decrypt() {
        password1 = UserState.decryptPassword(encryptedPassword)
        password2 = password1
    }

    fun delete() {
        group.passwords.remove(this)
    }

    companion object {
        private var lastId = 0L

        fun nextId(): Long {
            return ++lastId
        }
    }
}
