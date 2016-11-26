package nl.astraeus.spm.ws

import nl.astraeus.spm.web.SimpleWebSocket

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun start(ws: SimpleWebSocket, tk: Tokenizer) {
    // check login state
    // if not login, send login action
    // else
    // send groups to client

    ws.send("STARTED!")
}
