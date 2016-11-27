package nl.astraeus.spm.web

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import nl.astraeus.spm.model.User
import nl.astraeus.spm.util.DateFormatter
import nl.astraeus.spm.ws.CommandDispatcher
import nl.astraeus.spm.util.Tokenizer
import org.h2.command.Command
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:28
 */

class SimpleWebSocketServer(port: Int): NanoWSD(port) {
    val dir: File = File("web")

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SimpleWebSocketServer::class.java)
        val STATS: Logger = LoggerFactory.getLogger("STATS")
        val ACCESS: Logger = LoggerFactory.getLogger("ACCESS")
    }

    override fun openWebSocket(handshake: IHTTPSession?) = SimpleWebSocket(this, handshake)

    override fun serveHttp(session: IHTTPSession?): Response {
        val start = System.nanoTime()
        var result = NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 Not Found")

        try {
            if (session != null) {
                val uri = session.uri

                if (uri.contains("../")) {
                    result = NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_ACCEPTABLE, NanoHTTPD.MIME_PLAINTEXT, "406 Not Acceptable")
                } else {
                    val handler = handlers[uri]

                    if (handler != null) {
                        result = handler(session)
                    } else {

                        val file: File
                        if (uri == "/") {
                            file = File(dir, "index.html")
                        } else {
                            file = File(dir, uri)
                        }

                        if (file.exists()) {
                            val mimeType = mimeTypes[file.extension] ?: "plain/txt"

                            result = NanoHTTPD.newChunkedResponse(Response.Status.OK, mimeType, file.inputStream())
                        }
                    }
                }
            }
        } finally {
            STATS.info("Uri ${session?.uri} took ${(System.nanoTime() - start) * 1000000f}ms")
            ACCESS.info("${session?.remoteIpAddress} ${result.status.requestStatus} ${session?.method?.name} ${session?.uri}")
        }

        return result
    }
}

class SimpleWebSocket(server: SimpleWebSocketServer, handshake: NanoHTTPD.IHTTPSession?): NanoWSD.WebSocket(handshake) {
    val logger = LoggerFactory.getLogger(SimpleWebSocket::class.java)
    var user: User? = null

    override fun onOpen() {
        logger.info("Websocket opened")
    }

    override fun onClose(code: NanoWSD.WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {
        logger.info("Websocket close: $code")
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
        logger.info("Websocket pong")
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
        logger.info("Websocket message")
        val text = message?.textPayload

        if (text != null && text.isNotEmpty()) {
            CommandDispatcher.handle(this, text)
        }
    }

    override fun onException(exception: IOException?) {
        logger.info("Websocket exception: $exception")
    }

    fun send(vararg args: String) {
        send(Tokenizer.tokenize(*args))
    }
}
