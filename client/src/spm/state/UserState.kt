package spm.state

import spm.crypt.Aes
import spm.model.Group
import spm.ws.Tokenizer
import spm.ws.WebSocketConnection
import java.util.*

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 16:14
 */

object UserState {
    var loginname: String? = null
    set(value) {
        val crypto = js("CryptoJS")

        field = crypto.SHA256(value).toString()
    }
    var loginPasswordHash: String? = null
    var encryptedEncryptionKey: String? = null
    var loggedIn = false

    // groups
    var currentGroup: Group? = null
    var topGroup: Group? = null

    private var decryptPassphraseHash: String? = null

    fun clear() {
        loginname = null
        loginPasswordHash = null
        decryptPassphraseHash = null
        encryptedEncryptionKey = null
        topGroup = null
        currentGroup = null
        loggedIn = false
    }

    fun decryptPassword(password: String): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val eek: String = encryptedEncryptionKey ?: throw IllegalStateException("passphraseHash is not set")

        val decryptedEncryptionKey = Aes.decrypt(eek, pp).toString()

        return Aes.decrypt(password, decryptedEncryptionKey).toString()
    }

    fun encryptPassword(password: String): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val eek: String = encryptedEncryptionKey ?: throw IllegalStateException("passphraseHash is not set")

        val decryptedEncryptionKey = Aes.decrypt(eek, pp).toString()

        return Aes.encrypt(password, decryptedEncryptionKey).toString()
    }

    fun setPassword(password: String) {
        val crypto = js("CryptoJS")

        val sha256 = crypto.SHA256(password)
        val sha512 = crypto.SHA512(password)

        loginPasswordHash = "${js("CryptoJS.PBKDF2(sha256, sha512, { keySize: 256 / 32, iterations: 500 });")}";
        decryptPassphraseHash = "${js("CryptoJS.PBKDF2(sha256, sha512, { keySize: 256 / 32, iterations: 750 });")}"
    }

    /** create encryption key and return encrypted encryption key */
    fun createEncryptionKey(): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")

        val base64String = "${js("CryptoJS.enc.Base64.stringify(CryptoJS.lib.WordArray.random(64));")}"

        return Aes.encrypt(base64String, pp).toString()
    }

    fun loadData(data: String) {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val eek: String = encryptedEncryptionKey ?: throw IllegalStateException("encryptedEncryptionKey is not set")

        val decryptedEncryptionKey = Aes.decrypt(eek, pp).toString()
        val decryptedData = Aes.decrypt(data, decryptedEncryptionKey).toString()

        console.log("decrypted: ["+decryptedData+", "+decryptedData.length+"]")

        if (decryptedData.isBlank()) {
            topGroup = Group(0, "Root", false, null, false, ArrayList(), ArrayList())
        } else {
            topGroup = Group(Tokenizer(decryptedData))
        }

        console.log("topGroup: ["+topGroup?.name+"]")
    }

    fun saveData() {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val eek: String = encryptedEncryptionKey ?: throw IllegalStateException("passphraseHash is not set")

        val decryptedEncryptionKey = Aes.decrypt(eek, pp).toString()
        val tg = topGroup

        if (tg != null) {
            val export = tg.export()

            console.log("export: ["+export+"]")
            val data = Aes.encrypt(export, decryptedEncryptionKey).toString()

            WebSocketConnection.send("SAVEDATA", data)
        }
    }
}
