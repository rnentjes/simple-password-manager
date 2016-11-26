package spm

import spm.crypt.Aes
import spm.crypt.Hash
import spm.view.login.LoginView
import spm.ws.WebSocketConnection
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:24
 */

fun main(args: Array<String>) {
    val body = document.body ?: throw IllegalStateException("document.body not defined! Are you sure this is a browser?")

    body.appendChild(LoginView.create())

    WebSocketConnection.open()

    val sha1 = Hash.sha256("abc")
    val sha2 = Hash.sha256("cde")

    println("SHA256 'abc' = $sha1 -> 'cde' = $sha2")

    val message = "This is my message"
    val passphrase = "This is my passphraseHash"

    val hashed = Hash.sha512(passphrase)

    println("Hashed: $passphrase -> $hashed")

    val encrypted = Aes.encrypt(message, hashed.toString())

    println("Encrypted: $message -> $encrypted")

    val decrypted = Aes.decrypt(encrypted, hashed.toString())

    println("Decrypted: $message -> $decrypted")

}
