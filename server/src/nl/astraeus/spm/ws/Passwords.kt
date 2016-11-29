package nl.astraeus.spm.ws

import nl.astraeus.spm.model.*
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket
import java.util.*

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun getPasswords(ws: SimpleWebSocket, tk: Tokenizer) {
    val groupId = tk.next().toLong()

    val group = GroupDao.find(groupId) ?: throw IllegalAccessException("Group not found!")

    val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")

    sendPasswords(ws, group, user)
}

private fun sendPasswords(ws: SimpleWebSocket, group: Group, user: User) {
    if (group.user != user.name) {
        throw IllegalAccessException("You are not allowed to edit this group!")
    }

    val passwords = PasswordDao.findByGroup(group.id)
    val message = StringBuilder()

    message.append("SETPASSWORDS~${group.id}~${passwords.size}")

    for (password in passwords) {
        message.append("~${password.tokenize()}")
    }

    ws.send(message.toString())
}

fun newPassword(ws: SimpleWebSocket, tk: Tokenizer) {
    val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")
    val groupId = tk.next().toLong()

    val group = GroupDao.find(groupId) ?: throw IllegalAccessException("Group not found!")

    val title = tk.next()
    val url = tk.next()
    val username = tk.next()
    val encryptedPassword = tk.next()

    val password = Password(0, user.name, groupId, title, url, username, encryptedPassword, "", Date(), Date())

    PasswordDao.insert(password)

    sendPasswords(ws, group, user)
}
