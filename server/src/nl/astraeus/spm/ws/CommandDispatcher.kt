package nl.astraeus.spm.ws

import nl.astraeus.database.transaction
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by rnentjes on 7-6-16.
 */

object CommandDispatcher {
    val logger = LoggerFactory.getLogger(CommandDispatcher::class.java)
    val commandLogger = LoggerFactory.getLogger("WS")

    val commands: MutableMap<String, (ws: SimpleWebSocket, tk: Tokenizer) -> Unit> = HashMap()

    init {
        commands.put("OK", ::ok)
        commands.put("LOGIN", ::login)
        commands.put("REGISTER", ::register)
        commands.put("UPDATEGROUPNAME", ::updateGroupName)
        commands.put("CREATEGROUP", ::createGroup)
        commands.put("GROUPOPENED", ::openedGroup)
        commands.put("NEWPASSWORD", ::newPassword)
        commands.put("SAVEPASSWORD", ::savePassword)
        commands.put("DELETEPASSWORD", ::deletePassword)
    }

    fun handle(ws: SimpleWebSocket, msg: String) {
        val start = System.nanoTime()
        val tk = Tokenizer(msg)
        val cmd = tk.next()

        if (cmd == "OK") {
            // skip
            val time = (System.nanoTime() - start) / 1000000f
            commandLogger.debug(String.format("[%16s] %15s %12sms", cmd, ws.handshakeRequest.remoteIpAddress, time))
        } else {
            try {
                val command = commands[cmd] ?: throw IllegalStateException("Don't know how to handle command [$cmd]")

                commandLogger.debug("Handling $cmd")
                transaction {
                    command.invoke(ws, tk)
                }
            } catch(e: Exception) {
                logger.warn(e.message, e)

                ws.sendAlert("Error", "${e.message}")
            }

            val time = (System.nanoTime() - start) / 1000000f
            commandLogger.info(String.format("[%16s] %15s %12sms", cmd, ws.handshakeRequest.remoteIpAddress, time))
        }
    }

}
