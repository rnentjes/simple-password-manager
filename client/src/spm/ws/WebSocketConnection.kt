package spm.ws

import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import kotlin.browser.window

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 12:08
 */

object WebSocketConnection {
    var websocket: WebSocket? = null

    fun open() {
        close()

        websocket = WebSocket("ws://${window.location.hostname}:3456")

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
        ws.send("START")

        window.setInterval({
            ws.send("OK")
        }, 10000)
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
        println("Closed websocket! $ws")

        websocket = null

        return "dynamic"
    }

    fun onError(ws: WebSocket, event: Event): dynamic {
        println("Error websocket! $ws")

        websocket = null

        return "dynamic"
    }

}