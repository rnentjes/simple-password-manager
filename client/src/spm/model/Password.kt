package spm.model

import spm.state.UserState
import spm.ws.Tokenizer
import spm.ws.WebSocketConnection

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
    constructor(group: Group) : this(0, "", group, "", "", "", "", "", "", "")

    constructor(tk: Tokenizer, group: Group) : this(
      parseInt(tk.next()).toLong(),
      tk.next(),
      group,
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next(),
      "",
      "",
      tk.next())

    fun tokenized(): String = Tokenizer.tokenize("$id", user, title, website, username, encryptedPassword, description)

    fun decrypt() {
        password1 = UserState.decryptPassword(encryptedPassword)
        password2 = password1
    }

    fun delete() {
        group.passwords.remove(this)
    }
}
