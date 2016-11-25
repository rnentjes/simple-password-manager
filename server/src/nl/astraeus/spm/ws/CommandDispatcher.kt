package nl.astraeus.spm.ws

import nl.astraeus.spm.web.SimpleWebSocket
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by rnentjes on 7-6-16.
 */

object CommandDispatcher {
    val logger = LoggerFactory.getLogger("STATS")

    val commands: MutableMap<String, (ws: SimpleWebSocket, tk: Tokenizer) -> Unit> = HashMap()

    init {
        commands.put("START", ::start)
        commands.put("CREATEGROUP", ::createGroup)
    }

    fun handle(ws: SimpleWebSocket, msg: String) {
        val start = System.nanoTime()
        val tk = Tokenizer(msg)
        val cmd = tk.next()

        try {
            val command = commands[cmd] ?: throw IllegalStateException("Don't know how to handle command [$cmd]")

            command.invoke(ws, tk)
        } catch(e: Exception) {
            logger.warn(e.message, e)
        }

        val time = (System.nanoTime() - start) / 1000000f
        logger.info("Command $cmd took ${time}ms")
    }

}
