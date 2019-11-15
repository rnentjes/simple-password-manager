package spm.view

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.js.div
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.WebSocket
import spm.state.UserState
import spm.ws.CommandDispatcher
import spm.ws.Tokenizer

/**
 * Created by rnentjes on 3-4-17.
 */

class Main : Komponent() {

    init {
        CommandDispatcher.setLoginListener(this::login)
    }

    fun login(ws: WebSocket, tk: Tokenizer) {
        UserState.encryptedEncryptionKey = tk.next()
        UserState.loggedIn = true
        UserState.loadData(tk.next())
        UserState.readOnly = tk.next() == "true"

        refresh()
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div {
        if (!UserState.loggedIn) {
            include(Login())
        } else {
            include(Container(this@Main))
        }
    }

}
