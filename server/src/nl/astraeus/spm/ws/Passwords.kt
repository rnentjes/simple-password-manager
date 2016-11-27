package nl.astraeus.spm.ws

import nl.astraeus.spm.model.GroupDao
import nl.astraeus.spm.model.PasswordDao
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun getPasswords(ws: SimpleWebSocket, tk: Tokenizer) {
    val groupId = tk.next().toLong()

    val group = GroupDao.find(groupId)

    if (group != null) {
        val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")

        if (group.user != user.name) {
            throw IllegalAccessException("You are not allowed to edit this group!")
        }

        val passwords = PasswordDao.findByGroup(groupId)
        val message = StringBuilder()

        message.append("SETPASSWORDS~$groupId~${passwords.size}")

        for (password in passwords) {
            message.append("~${password.tokenize()}")
        }

        ws.send(message.toString())
    }
}
