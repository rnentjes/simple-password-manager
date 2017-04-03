package spm.komp

import kotlinx.html.TagConsumer
import kotlinx.html.js.div
import nl.astraeus.komp.HtmlComponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.WebSocket
import spm.state.UserState
import spm.ws.CommandDispatcher
import spm.ws.Tokenizer

/**
 * Created by rnentjes on 3-4-17.
 */

class Main : HtmlComponent() {
    init {
        CommandDispatcher.setLoginListener(this::login)
    }

    fun login(ws: WebSocket, tk: Tokenizer) {
        UserState.encryptedEncryptionKey = tk.next()
        UserState.loggedIn = true
        UserState.loadData(tk.next())

        refresh()
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div {
        if (!UserState.loggedIn) {
            include(Login())
        } else {
            include(Container())
        }
    }

}
