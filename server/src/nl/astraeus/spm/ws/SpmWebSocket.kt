package nl.astraeus.spm.ws

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import org.eclipse.jetty.websocket.servlet.WebSocketCreator
import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpSession

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:28
 */


class SimpleWebSocketServlet: WebSocketServlet() {

    override fun configure(factory: WebSocketServletFactory) {
        val creator = factory.creator
        factory.creator = WebSocketCreator { servletUpgradeRequest, servletUpgradeResponse ->
            val result = creator.createWebSocket(servletUpgradeRequest, servletUpgradeResponse)

            if (result is SimpleWebSocket) {
                result.httpSession = servletUpgradeRequest.httpServletRequest.getSession(true)
            }

            result
        }

        factory.register(SimpleWebSocket::class.java)
    }

}

class SimpleWebSocket(): WebSocketAdapter() {
    var logger = LoggerFactory.getLogger(SimpleWebSocket::class.java)
    var statsLogger = LoggerFactory.getLogger("STATS")
    var songId: String = ""
    var httpSession: HttpSession? = null


    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)

        logger.info("Closed websocket, status: $statusCode, reason: $reason")
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {}


    override fun onWebSocketError(cause: Throwable?) {
        logger.info("Websocket connected, session: ${cause?.message}, adaptor: $this")
    }

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)

        logger.info("Connect from (http) session: $httpSession")

//        val hs = httpSession
//        if (hs != null) {
//            logger.info("GITHUB code: ${hs.getAttribute("github_code")}")
//        }

        logger.info("Websocket connected, session: $sess, adaptor: $this")
    }

    override fun onWebSocketText(message: String) {
        if (message != "OK") {
            CommandDispatcher.handle(this, message)
        }
    }

}
