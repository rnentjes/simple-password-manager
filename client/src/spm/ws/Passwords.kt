package spm.ws

import org.w3c.dom.WebSocket
import spm.view.password.Password
import spm.view.password.PasswordOverviewView
import java.util.*

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun setPasswords(ws: WebSocket, tk: Tokenizer) {
    val groupId = parseInt(tk.next())
    val numberOfPasswords = parseInt(tk.next())
    val passwords: MutableList<Password> = ArrayList()

    for (index in 0..numberOfPasswords-1) {
        val password = Password(tk)

        passwords.add(password)
    }

    PasswordOverviewView.show(passwords)
}
