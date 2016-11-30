package spm.state

import spm.crypt.Aes
import spm.crypt.Hash
import spm.crypt.PBKDF2
import spm.view.group.Group
import spm.ws.WebSocketConnection
import kotlin.browser.window

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 16:14
 */

object UserState {
    var loginname: String? = null
    var loginPasswordHash: String? = null
    var encryptedEncryptionKey: String? = null

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

        println("sha256: $sha256")
        println("sha512: $sha512")

        loginPasswordHash = "${js("CryptoJS.PBKDF2(sha256, sha512, { keySize: 256 / 32, iterations: 500 });")}";
        decryptPassphraseHash = "${js("CryptoJS.PBKDF2(sha256, sha512, { keySize: 256 / 32, iterations: 750 });")}"
    }

    /** create encryption key and return encrypted encryption key */
    fun createEncryptionKey(): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")

        val base64String = "${js("CryptoJS.enc.Base64.stringify(CryptoJS.lib.WordArray.random(64));")}"

        return Aes.encrypt(base64String, pp).toString()
    }
}
