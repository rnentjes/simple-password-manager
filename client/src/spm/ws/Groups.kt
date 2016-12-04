package spm.ws

import org.w3c.dom.WebSocket
import spm.state.UserState
import spm.view.group.Group
import spm.view.group.GroupView
import java.util.*

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun setGroups(ws: WebSocket, tk: Tokenizer) {
    val root = Group(tk)
    val selected = UserState.currentGroup
    val opened = HashMap <Long, Boolean>()

    for (group in root.all()) {
        val found = opened[group.id]

        if (found != null) {
            group.opened = found
        }

        if (group.id == selected?.id) {
            UserState.currentGroup = selected
        }
    }

    GroupView.show(root)
}

fun createdGroup(ws: WebSocket, tk: Tokenizer) {


    WebSocketConnection.doneLoading()
}