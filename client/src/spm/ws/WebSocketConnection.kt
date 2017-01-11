package spm.ws

import org.w3c.dom.Element
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import spm.state.UserState
import spm.view.*
import spm.view.main.MainView
import spm.view.modal.ModalView
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

        if (window.location.hostname.contains("localhost")) {
            websocket = WebSocket("ws://${window.location.hostname}:3456")
        } else {
            websocket = WebSocket("wss://${window.location.hostname}")
        }

        val ws = websocket

        if (ws != null) {
            ws.onopen       = { onOpen(ws, it)      }
            ws.onmessage    = { onMessage(ws, it)   }
            ws.onclose      = { onClose(ws, it)     }
            ws.onerror      = { onError(ws, it)     }
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

                ModalView.showAlert("Error", "Connection to the server was lost!\nPlease try again later.")
                WebSocketConnection.loading()
                reconnect()
            }
        }, 10000)
    }

    fun reconnect() {
        val actualWs = websocket

        if (actualWs != null) {
            ModalView.showAlert("Succes", "Connection with the server was restored!")
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

        if(websocket == null) {
            if (!UserState.loggedIn) {
                UserState.clear()
            }
            ModalView.showAlert("Error", "Cannot connect to the server!")
        }
    }

    fun send(vararg args: String) {
        send(Tokenizer.tokenize(*args))
    }

    fun getLoadingDiv(): Element {
        val result: Element

        if (!hasElem("loading_div")) {
            result = div().attr("id", "loading_div").cls("loading").txt("Loading&8230;")

            val body = document.body ?: throw IllegalStateException("The body was not found!")
            body.add { result }
        } else {
            result = elem("loading_div")
        }

        return result
    }

    fun loadingWork(callback: () -> Unit = {}) {
        loadingCalls++

        if (loadingCalls >= 1) {
            // hide interface
            getLoadingDiv().attr("style", "display: block;")
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
            getLoadingDiv().attr("style", "display: block;")
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
            getLoadingDiv().attr("style", "display: none;")
        }
    }

}
