package spm.ws

import org.w3c.dom.WebSocket
import spm.mainComponent
import spm.state.UserState
import spm.view.Modal

/**
 * Created by rnentjes on 7-6-16.
 */

private var nextCallbackId = 0L
fun nextCallbackId(): Long = ++nextCallbackId;

object CommandDispatcher {
    val commands: MutableMap<String, (ws: org.w3c.dom.WebSocket, tk: spm.ws.Tokenizer) -> Unit> = HashMap()
    var loginListener: ((WebSocket, Tokenizer) -> Unit)? = null
    val callbacks: MutableMap<String, (WebSocket, Tokenizer) -> Unit> = mutableMapOf()

    // LOCKED, obtained lock
    // BLOCKED, unable to get lock
    // UNLOCKED, nobody has lock
    init {
        commands["LOGIN"] = this::login
        commands["ALERT"] = { ws, tk -> Modal.showAlert(tk.next(), tk.next()) }
        commands["RESPONSE"] = { ws, tk ->
            val callbackId = tk.next()

            callbacks[callbackId]?.invoke(ws, tk)
            callbacks.remove(callbackId)
        }
        commands["PASSWORD_UPDATED"] = { ws, tk ->
            Modal.showAlert("Success", "Password successfully updated!")
        }
        commands["UNLOCKED"] = { ws, tk ->
            UserState.readOnly = false
            UserState.obtainedLock = false

            val currentGroupId: Long? = UserState.currentGroup?.id
            UserState.loadData(tk.next())
            currentGroupId?.let { id ->
                UserState.currentGroup = UserState.topGroup?.findById(id)
            }

            mainComponent.refresh()
        }
        commands["BLOCKED"] = { ws, tk ->
            UserState.readOnly = true
            UserState.obtainedLock = false

            mainComponent.refresh()
        }
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
