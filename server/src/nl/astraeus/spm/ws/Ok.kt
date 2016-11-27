package nl.astraeus.spm.ws

import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * User: rnentjes
 * Date: 27-11-16
 * Time: 12:58
 */

private val logger: Logger = LoggerFactory.getLogger("nl.astraeus.spm.ws.Ok")

fun ok(ws: SimpleWebSocket, tk: Tokenizer) {
    logger.debug("Ok - ${ws.handshakeRequest.remoteIpAddress}")
}
