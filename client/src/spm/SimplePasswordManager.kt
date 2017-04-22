package spm

import nl.astraeus.komp.Komp
import spm.view.Main
import spm.ws.WebSocketConnection
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:24
 */

fun main(args: Array<String>) {
    Komp.create(document.body!!, Main())

    WebSocketConnection.open()
}
