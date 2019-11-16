package nl.astraeus.spm.ws

import nl.astraeus.database.transaction
import nl.astraeus.spm.crypt.PasswordHash
import nl.astraeus.spm.model.Lock
import nl.astraeus.spm.model.LockDao
import nl.astraeus.spm.model.User
import nl.astraeus.spm.model.UserDao
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket
import nl.astraeus.spm.web.connections
import nl.astraeus.spm.web.userLock
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by rnentjes on 7-6-16.
 */

fun login(ws: SimpleWebSocket, tk: Tokenizer) {
    val loginName = tk.next()
    val passwordHash = tk.next()

    val found = UserDao.findByName(loginName)

    if (found != null && PasswordHash.validatePassword(passwordHash, found.password)) {
        ws.user = found
        ws.send("LOGIN", found.encryptedKey, found.getData(), (userLock[found.id]?.get() == ws).toString())
        if (userLock[ws.user?.id]?.get() != null) {
            ws.send("BLOCKED")
        }
    } else {
        ws.sendAlert("Error", "Unable to authenticate user!")
    }
}

fun logout(ws: SimpleWebSocket, tk: Tokenizer) {
    ws.logout()
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
        ws.send("LOGIN", encryptedKey, user.getData())
    }
}

fun lock(ws: SimpleWebSocket, tk: Tokenizer) {
    val responseId = tk.next()

    ws.user?.let {
        userLock.putIfAbsent(it.id, AtomicReference(null))

        if (userLock[it.id]?.compareAndSet(null, ws) == true) {
            for ((_, cws) in connections) {
                if (cws.user == it && cws != ws) {
                    cws.send("BLOCKED")
                }
            }

            ws.send("RESPONSE", responseId, "LOCKED")
        } else {
            ws.send("RESPONSE", responseId, "LOCK_FAILED")
        }
    }
}

fun unlock(ws: SimpleWebSocket, tk: Tokenizer) {
    ws.user?.let {
        userLock.computeIfAbsent(it.id) { _ ->
            AtomicReference(null)
        }
        if (userLock[it.id]?.compareAndSet(ws, null) == true) {
            for ((_, cws) in connections) {
                if (cws.user == it) {
                    cws.user?.let { user ->
                        val foundUser = UserDao.find(user.id)
                        cws.send("UNLOCKED", foundUser.getData())
                        cws.user = foundUser
                    }
                }
            }
        } else {
            ws.send("BLOCKED")
        }
    }
}

fun saveData(ws: SimpleWebSocket, tk: Tokenizer) {
    val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")
    val data = tk.next()

    transaction {
        user.setData(data)
        user.updated = Date()
        UserDao.update(user)
    }

    unlock(ws, tk)
}

fun updatePassword(ws: SimpleWebSocket, tk: Tokenizer) {
    val user = ws.user ?: throw IllegalAccessException("No loggedin user found!")
    val name = tk.next()
    val password = tk.next()
    val encryptedEncryptionKey = tk.next()

    if (name.equals(user.name)) {
        val passwordHash = PasswordHash.createHash(password)

        transaction {
            user.encryptedKey = encryptedEncryptionKey
            user.password = passwordHash
            user.updated = Date()

            UserDao.update(user)

            ws.send("PASSWORD_UPDATED")
        }
    } else {
        ws.sendAlert("Error", "Wrong user send to server!?")
    }
}

object CommandDispatcher {
    val logger = LoggerFactory.getLogger(CommandDispatcher::class.java)
    val commandLogger = LoggerFactory.getLogger("WS")

    val commands: MutableMap<String, (ws: SimpleWebSocket, tk: Tokenizer) -> Unit> = HashMap()

    init {
        commands["OK"] = ::ok
        commands["LOGIN"] = ::login
        commands["LOCK"] = ::lock
        commands["UNLOCK"] = ::unlock
        commands["REGISTER"] = ::register
        commands["SAVEDATA"] = ::saveData
        commands["LOGOUT"] = ::logout
        commands["UPDATE_PASSWORD"] = ::updatePassword
    }

    fun handle(ws: SimpleWebSocket, msg: String) {
        val start = System.nanoTime()
        val tk = Tokenizer(msg)
        val cmd = tk.next()

        if (cmd == "OK") {
            // skip
            val time = (System.nanoTime() - start) / 1000000f
            commandLogger.debug(String.format("[%16s] %15s %12sms", cmd, ws.handshakeRequest.remoteIpAddress, time))
        } else {
            try {
                val command = commands[cmd] ?: throw IllegalStateException("Don't know how to handle command [$cmd]")

                commandLogger.debug("Handling $cmd")
                transaction {
                    command.invoke(ws, tk)
                }
            } catch (e: Exception) {
                logger.warn(e.message, e)

                ws.sendAlert("Error", "${e.message}")
            }

            val time = (System.nanoTime() - start) / 1000000f
            commandLogger.info(String.format("[%16s] %15s %12sms", cmd, ws.handshakeRequest.remoteIpAddress, time))
        }
    }

}
