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

fun main(args: Array<String>) {
    val splash = document.getElementById("splash")

    splash?.parentElement?.removeChild(splash)

    Komponent.create(document.body!!, mainComponent)

    WebSocketConnection.open()
}
