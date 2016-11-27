package spm.view.password

import org.w3c.dom.Element
import spm.view.div
import spm.view.group.Group
import spm.ws.Tokenizer

/**
 * User: rnentjes
 * Date: 27-11-16
 * Time: 16:20
 */

data class Password(
  var id: Long,
  var title: String,
  var username: String,
  var url: String,
  var encryptedPassword: String,
  var notes: String
) {
    constructor(): this(0, "", "", "", "", "")

    constructor(tk: Tokenizer): this(parseInt(tk.next()).toLong(), tk.next(), tk.next(), tk.next(), tk.next(), tk.next())

    fun tokenized(): String = Tokenizer.tokenize("$id", title, username, url, encryptedPassword, notes)
}

object PasswordOverviewView {

    fun create(group: Group): Element {
        return div()
    }

}
