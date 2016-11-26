package spm

import spm.crypt.Aes
import spm.view.login.LoginView
import spm.ws.WebSocketConnection
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:24
 */

fun main(args: Array<String>) {
    val body = document.body ?: throw IllegalStateException("document.body not defined! Are you sure this is a browser?")

    body.appendChild(LoginView.create())

    WebSocketConnection.open()

    val sha1 = Aes.sha256("abc")
    val sha2 = Aes.sha256("cde")

    println("SHA256 'abc' = $sha1 -> 'cde' = $sha2")
}
