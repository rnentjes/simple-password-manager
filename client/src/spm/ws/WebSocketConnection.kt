package spm.ws

import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.js.div
import org.w3c.dom.HTMLElement
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import spm.state.UserState
import spm.view.Modal
import spm.view.RemovePasswordConfirm
import kotlin.browser.document
import kotlin.browser.window

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 12:08
 */

object WebSocketConnection {
    var websocket: WebSocket? = null
    var loadingCalls: Int = 0
    var interval: Int = 0

    fun open() {
        close()

        if (window.location.hostname.contains("localhost") || window.location.hostname.contains("192.168")) {
            websocket = WebSocket("ws://${window.location.hostname}:${window.location.port}/ws")
        } else {
            websocket = WebSocket("wss://${window.location.hostname}/ws")
        }

        val ws = websocket

        if (ws != null) {
            ws.onopen = { onOpen(ws, it) }
            ws.onmessage = { onMessage(ws, it) }
            ws.onclose = { onClose(ws, it) }
            ws.onerror = { onError(ws, it) }
        }
    }

    fun close() {
        websocket?.close(-1, "Application closed socket.")
    }

    fun onOpen(ws: WebSocket, event: Event) {
        WebSocketConnection.doneLoading()

        if (UserState.loginname != null && UserState.loginPasswordHash != null) {
            send("LOGIN",
              UserState.loginname ?: throw IllegalStateException("Whut!"),
              UserState.loginPasswordHash ?: throw IllegalStateException("Whut!")
            )
        }

        interval = window.setInterval({
            val actualWs = websocket

            if (actualWs != null) {
                ws.send("OK")
            } else {
                window.clearInterval(interval)
                //MainView.logout()

                Modal.showAlert("Error", "Connection to the server was lost!\nPlease try again later.")
                WebSocketConnection.loading()
                reconnect()
            }
        }, 10000)
    }

    fun reconnect() {
        val actualWs = websocket

        if (actualWs != null) {
            Modal.showAlert("Succes", "Connection with the server was restored!")
        } else {
            open()

            window.setTimeout({
                reconnect()
            }, 1000)
        }
    }

    fun onMessage(ws: WebSocket, event: Event) {
        if (event is MessageEvent) {
            val data = event.data

            if (data is String) {
                CommandDispatcher.handle(ws, data)
            }
        }
    }

    fun onClose(ws: WebSocket, event: Event): dynamic {
        websocket = null

        return "dynamic"
    }

    fun onError(ws: WebSocket, event: Event): dynamic {
        println("Error websocket! $ws")

        websocket = null

        return "dynamic"
    }

    fun send(message: String) {
        websocket?.send(message)

        if (websocket == null) {
            if (!UserState.loggedIn) {
                UserState.clear()
            }
            Modal.showAlert("Error", "Cannot connect to the server!")
        }
    }

    fun send(vararg args: String) {
        send(Tokenizer.tokenize(*args))
    }

    fun lock(callback: (WebSocket, Tokenizer) -> Unit) {
        val nextId = "${nextCallbackId()}"
        CommandDispatcher.callbacks[nextId] = callback
        send(Tokenizer.tokenize("LOCK", nextId))
    }

    fun getLoadingDiv(): HTMLElement {
        var result = document.getElementById("loading_div")

        if (result == null) {
            result = document.create.div(classes = "loading") {
                id = "loading_div"
                +"Loading&8230;"
            }
            document.body?.appendChild(result)
        }

        return result as HTMLElement
    }

    fun loadingWork(callback: () -> Unit = {}) {
        loadingCalls++

        if (loadingCalls >= 1) {
            // hide interface
            getLoadingDiv().style.display = "block"
        }

        window.requestAnimationFrame {
            window.requestAnimationFrame {
                try {
                    callback()
                } finally {
                    doneLoading()
                }
            }
        }
    }

    fun loading(callback: () -> Unit = {}) {
        loadingCalls++

        if (loadingCalls >= 1) {
            // hide interface
            getLoadingDiv().style.display = "block"
        }

        try {
            callback()
        } finally {
            doneLoading()
        }
    }

    fun doneLoading() {
        if (loadingCalls > 0) {
            loadingCalls--
        }

        if (loadingCalls == 0) {
            // show interface
            getLoadingDiv().style.display = "none"
        }
    }

}
