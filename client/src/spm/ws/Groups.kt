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
    val old = UserState.topGroup
    val selected = UserState.currentGroup
    val opened = HashMap <Long, Boolean>()

    if (old != null) {
        for (group in old.all()) {
            opened.put(group.id, group.opened)
        }
    }

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