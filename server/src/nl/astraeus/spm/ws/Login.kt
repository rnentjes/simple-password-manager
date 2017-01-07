package nl.astraeus.spm.ws

import nl.astraeus.spm.crypt.PasswordHash
import nl.astraeus.spm.model.User
import nl.astraeus.spm.model.UserDao
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 15:28
 */

fun login(ws: SimpleWebSocket, tk: Tokenizer) {
    val loginName = tk.next()
    val passwordHash = tk.next()

    val found = UserDao.findByName(loginName)

    if (found != null && PasswordHash.validatePassword(passwordHash, found.password)) {
        ws.user = found
        ws.send("LOGIN", found.encryptedKey)

        sendGroups(ws)
    } else {
        ws.sendAlert("Error", "Unable to authenticate user $loginName!")
    }
}

fun register(ws: SimpleWebSocket, tk: Tokenizer) {
    val loginName = tk.next()
    val passwordHash = tk.next()
    val encryptedKey = tk.next()

    val password = PasswordHash.createHash(passwordHash)

    val found = UserDao.findByName(loginName)

    if (found != null) {
        ws.sendAlert("Error", "Username already taken!")
    } else {
        val user = User(loginName, password, encryptedKey)

        UserDao.insert(user)

        ws.user = user
        ws.send("LOGIN", encryptedKey)

        sendGroups(ws)
    }
}
