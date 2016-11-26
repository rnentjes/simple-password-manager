package spm

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
}
