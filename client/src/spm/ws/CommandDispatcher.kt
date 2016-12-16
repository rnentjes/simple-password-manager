package spm.ws

import org.w3c.dom.WebSocket
import spm.view.modal.ModalView

/**
 * Created by rnentjes on 7-6-16.
 */

object CommandDispatcher {
    val commands: MutableMap<String, (ws: org.w3c.dom.WebSocket, tk: spm.ws.Tokenizer) -> Unit> = java.util.HashMap()

    init {
        commands.put("LOGIN", ::login)
        commands.put("ALERT", { ws, tk -> ModalView.showAlert(tk.next(), tk.next()) })
        commands.put("SETGROUPS", ::setGroups)
        commands.put("CREATEDGROUP", ::createdGroup)
        commands.put("SAVEDPASSWORD", ::savedPassword)
        commands.put("DELETEDPASSWORD", ::deletedPassword)
    }

    fun handle(ws: WebSocket, msg: String) {
        val tk = Tokenizer(msg)
        val cmd = tk.next()

        val command = commands[cmd] ?: throw IllegalStateException("Don't know how to handle command [$cmd]")

        command.invoke(ws, tk)
    }
}
