package nl.astraeus.spm.web

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import nl.astraeus.database.transaction
import nl.astraeus.spm.model.LockDao
import nl.astraeus.spm.model.User
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.ws.CommandDispatcher
import nl.astraeus.spm.ws.unlock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:28
 */

class SimpleWebSocketServer(port: Int) : NanoWSD(port) {
    val dir: File = File("web")

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SimpleWebSocketServer::class.java)
        val STATS: Logger = LoggerFactory.getLogger("STATS")
        val ACCESS: Logger = LoggerFactory.getLogger("ACCESS")
    }

    override fun openWebSocket(handshake: NanoHTTPD.IHTTPSession?) = SimpleWebSocket(this, handshake)

    override fun serveHttp(session: NanoHTTPD.IHTTPSession?): NanoHTTPD.Response {
        val start = System.nanoTime()
        var result = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 Not Found")

        try {
            if (session != null) {
                val uri = session.uri

                if (uri.contains("../")) {
                    result = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_ACCEPTABLE, NanoHTTPD.MIME_PLAINTEXT, "406 Not Acceptable")
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

                            result = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeType, file.inputStream())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn(e.message, e)
        } finally {
            STATS.info("Uri ${session?.uri} took ${(System.nanoTime() - start) * 1000000f}ms")
            ACCESS.info("${session?.remoteIpAddress} ${result.status.requestStatus} ${session?.method?.name} ${session?.uri}")
        }

        return result
    }
}

val connections: MutableMap<Long, SimpleWebSocket> = ConcurrentHashMap()
val userLock: MutableMap<Long, AtomicReference<SimpleWebSocket?>> = ConcurrentHashMap()
var wsId: Long = 0

fun getNextWSId(): Long {
    synchronized(wsId) {
        return ++wsId
    }
}

class SimpleWebSocket(server: SimpleWebSocketServer, handshake: NanoHTTPD.IHTTPSession?) : NanoWSD.WebSocket(handshake) {
    val id = getNextWSId()
    val logger = LoggerFactory.getLogger(SimpleWebSocket::class.java)
    var user: User? = null

    override fun onOpen() {
        logger.info("Websocket opened")

        connections[id] = this
    }

    override fun onClose(code: NanoWSD.WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {
        logger.info("Websocket close: $code")

        logout()
        connections.remove(this@SimpleWebSocket.id)
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
        logger.info("Websocket pong")
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
        val text = message?.textPayload

        if (text != null && text.isNotEmpty()) {
            CommandDispatcher.handle(this, text)
        }
    }

    override fun onException(exception: IOException?) {
        logger.info("Websocket exception: $exception")
    }

    fun logout() {
        user?.let {
            unlock(this, Tokenizer(""))
        }
        user = null
    }

    fun send(vararg args: String) {
        send(Tokenizer.tokenize(*args))
    }

    fun sendAlert(title: String, message: String) {
        send("ALERT", title, message)
    }
}
