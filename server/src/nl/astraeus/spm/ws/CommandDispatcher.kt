package nl.astraeus.spm.ws

import nl.astraeus.database.Persister
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by rnentjes on 7-6-16.
 */

object CommandDispatcher {
    val logger = LoggerFactory.getLogger("WS")

    val commands: MutableMap<String, (ws: SimpleWebSocket, tk: Tokenizer) -> Unit> = HashMap()

    init {
        commands.put("OK", ::ok)
        commands.put("CREATEGROUP", ::createGroup)
        commands.put("LOGIN", ::login)
        commands.put("REGISTER", ::register)
    }

    fun handle(ws: SimpleWebSocket, msg: String) {
        val start = System.nanoTime()
        val tk = Tokenizer(msg)
        val cmd = tk.next()

        if (cmd == "OK") {
            // skip
            val time = (System.nanoTime() - start) / 1000000f
            logger.debug(String.format("[%12s] %15s %12sms", cmd, ws.handshakeRequest.remoteIpAddress, time))
        } else {
            try {
                val command = commands[cmd] ?: throw IllegalStateException("Don't know how to handle command [$cmd]")

                Persister.begin()

                command.invoke(ws, tk)

                Persister.commit()
            } catch(e: Exception) {
                Persister.rollback()
                logger.warn(e.message, e)
            }

            val time = (System.nanoTime() - start) / 1000000f
            logger.info(String.format("[%12s] %15s %12sms", cmd, ws.handshakeRequest.remoteIpAddress, time))
        }

    }

}
