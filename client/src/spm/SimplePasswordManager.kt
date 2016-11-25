package spm

import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import spm.view.login.LoginView
import kotlin.browser.document
import kotlin.browser.window

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:24
 */

fun main(args: Array<String>) {
    println("ACTION!")

    val body = document.body ?: throw IllegalStateException("document.body not defined! Are you sure this is a browser?")

    body.appendChild(LoginView.create())

    val ws = WebSocket("ws://${window.location.hostname}:3456")

    ws.onopen = {
        ws.send("START")

        window.setInterval({
            ws.send("OK")
        }, 10000)
    }

    ws.onmessage = {
        if (it is MessageEvent) {
            if (it.data is String) {
                console.log("Message: ${it.data}")
            }
        }
    }

    ws.onclose = { e ->
        println("Closed websocket! $e")

        "dynamic"
    }

    ws.onerror = { e ->
        println("Error websocket! $e")

        "dynamic"
    }
}
