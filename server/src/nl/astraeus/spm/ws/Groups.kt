package nl.astraeus.spm.ws

import nl.astraeus.spm.model.Group
import nl.astraeus.spm.model.GroupDao
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun createGroup(ws: SimpleWebSocket, tk: Tokenizer) {
    val parentId = tk.next().toLong()
    val name = tk.next()

    val parent = GroupDao.find(parentId)

    if (parent != null) {
        val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")

        if (parent.user != user.name) {
            throw IllegalAccessException("You are not allowed to edit this group!")
        }

        val child = Group(user, name, parentGroup = parentId)

        GroupDao.insert(child)

        parent.opened = true

        GroupDao.update(parent)

        sendGroups(ws)
    }
}

fun updateGroupName(ws: SimpleWebSocket, tk: Tokenizer) {
    val id = tk.next().toLong()
    val name = tk.next()

    val group = GroupDao.find(id)

    if (group != null) {
        if (group.user != ws.user?.name) {
            throw IllegalAccessException("You are not allowed to edit this group!")
        }

        group.name = name

        GroupDao.update(group)

        sendGroups(ws)
    } else {
        throw IllegalAccessException("Group not found!")
    }
}

fun sendGroups(ws: SimpleWebSocket) {
    val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")

    var root = GroupDao.findRootGroupOfUser(user.name)

    if (root == null) {
        root = Group(user, "Root")

        GroupDao.insert(root)
    }

    ws.send("SETGROUPS~${root.tokenizeForClient()}")
}

fun openedGroup(ws: SimpleWebSocket, tk: Tokenizer) {
    val groupId = tk.next().toLong()
    val opened = tk.next() == "true"

    val group = GroupDao.find(groupId)

    if (group != null) {
        val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")

        if (group.user != user.name) {
            throw IllegalAccessException("You are not allowed to edit this group!")
        }

        group.opened = opened

        GroupDao.update(group)
    }
}

fun saveData(ws: SimpleWebSocket, tk: Tokenizer) {

}
