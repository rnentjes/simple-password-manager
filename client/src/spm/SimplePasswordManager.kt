package spm

import nl.astraeus.komp.Komponent
import spm.view.Main
import spm.ws.WebSocketConnection
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:24
 */

val mainComponent = Main()

fun main() {
    val splash = document.getElementById("splash")

    splash?.parentElement?.removeChild(splash)

    Komponent.logRenderEvent = true
    Komponent.logReplaceEvent = true

    Komponent.create(document.body!!, mainComponent)

    WebSocketConnection.open()
}
