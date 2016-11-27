package spm.ws

import org.w3c.dom.WebSocket
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
    val old = GroupView.currentGroup
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
    }

    GroupView.show(root)
}

fun createdGroup(ws: WebSocket, tk: Tokenizer) {


    WebSocketConnection.doneLoading()
}