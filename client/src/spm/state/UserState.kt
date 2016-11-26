package spm.state

import spm.crypt.Aes

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 16:14
 */

object UserState {
    var loginname: String? = null
    var loginPasswordHash: String? = null
    var decryptPassphraseHash: String? = null
    var encryptedEncryptionKey: String? = null

    fun decryptPassword(password: String): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val eek: String = encryptedEncryptionKey ?: throw IllegalStateException("passphraseHash is not set")

        val decryptedEncryptionKey = Aes.decrypt(eek, pp)

        return Aes.decrypt(password, decryptedEncryptionKey)
    }

    fun encryptPassword(password: String): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val eek: String = encryptedEncryptionKey ?: throw IllegalStateException("passphraseHash is not set")

        val decryptedEncryptionKey = Aes.decrypt(eek, pp)

        return Aes.encrypt(password, decryptedEncryptionKey)
    }

    /** return encrypted encryption key */
    val chars = "0123456789abcdef"
    fun createEncryptionKey(): String {
        val pp: String = decryptPassphraseHash ?: throw IllegalStateException("passphraseHash is not set")
        val builder = StringBuilder()

        // TODO: better random, better encryption key
        for (index in 0..31) {
            builder.append(chars[(Math.random() * 16).toInt()])
        }

        val eek = Aes.encrypt(builder.toString(), pp).toString()

        return eek
    }
}
