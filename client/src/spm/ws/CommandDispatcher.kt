package spm.ws

import org.w3c.dom.WebSocket
import stats.view.Modal

/**
 * Created by rnentjes on 7-6-16.
 */

//language=JSON
val html = ""

object CommandDispatcher {
    val commands: MutableMap<String, (ws: org.w3c.dom.WebSocket, tk: spm.ws.Tokenizer) -> Unit> = HashMap()
    var loginListener: ((WebSocket, Tokenizer) -> Unit)? = null

    init {
        commands.put("LOGIN", this::login)
        commands.put("ALERT", { ws, tk -> Modal.showAlert(tk.next(), tk.next()) })
    }

    fun login(ws: WebSocket, tk: Tokenizer) {
        val ll = loginListener

        if (ll != null) {
            ll(ws, tk)
        }
    }

    fun handle(ws: WebSocket, msg: String) {
        val tk = Tokenizer(msg)
        val cmd = tk.next()

        val command = commands[cmd] ?: throw IllegalStateException("Don't know how to handle command [$cmd]")

        command.invoke(ws, tk)
    }

    fun setLoginListener(func: (WebSocket, Tokenizer) -> Unit) {
        loginListener = func
    }
}
