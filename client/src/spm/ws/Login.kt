package spm.ws

import org.w3c.dom.WebSocket
import spm.state.UserState
import spm.view.main.MainView

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 15:20
 */


fun login(ws: WebSocket, tk: Tokenizer) {
    UserState.encryptedEncryptionKey = tk.next()
    UserState.loggedIn = true

    MainView.show()
}
