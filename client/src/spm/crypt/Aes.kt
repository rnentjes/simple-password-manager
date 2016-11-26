package spm.crypt

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 12:51
 *
 * requires: <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.2/rollups/aes.js"></script>
 * requires: <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.2/rollups/sha256.js"></script>
 * requires: <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.2/rollups/sha512.js"></script>
 */

object Aes {

    fun encrypt(plaintext: String, passphrase: String) = js("CryptoJS.AES.encrypt(plaintext, passphrase);")

    fun decrypt(encrypted: String, passphrase: String) = js("CryptoJS.AES.decrypt(encrypted, passphrase).toString(CryptoJS.enc.Utf8);")

    fun sha256(plaintext: String) = js("CryptoJS.SHA256(plaintext);")

    fun sha512(plaintext: String) = js("CryptoJS.SHA512(plaintext);")

}
