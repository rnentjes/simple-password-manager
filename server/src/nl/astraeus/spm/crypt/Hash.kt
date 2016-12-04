package nl.astraeus.spm.crypt

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import java.math.BigInteger
import java.security.SecureRandom


/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 15:44
 */

object Hash {

    fun sha256(base: String): String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(base.toByteArray(charset("UTF-8")))
            val hexString = StringBuffer()

            for (i in hash.indices) {
                val hex = Integer.toHexString(0xff and hash[i].toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }

            return hexString.toString()
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }
}

/** taken from: https://gist.github.com/jtan189/3804290 */
object PasswordHash {
    val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"

    // The following constants may be changed without breaking existing hashes.
    val SALT_BYTES = 24
    val HASH_BYTES = 24
    val PBKDF2_ITERATIONS = 1000

    val ITERATION_INDEX = 0
    val SALT_INDEX = 1
    val PBKDF2_INDEX = 2

    /**
     * Returns a salted PBKDF2 hash of the password.

     * @param   password    the password to hash
     * *
     * @return              a salted PBKDF2 hash of the password
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun createHash(password: String): String {
        return createHash(password.toCharArray())
    }

    /**
     * Returns a salted PBKDF2 hash of the password.

     * @param   password    the password to hash
     * *
     * @return              a salted PBKDF2 hash of the password
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun createHash(password: CharArray): String {
        // Generate a random salt
        val random = SecureRandom()
        val salt = ByteArray(SALT_BYTES)
        random.nextBytes(salt)

        // Hash the password
        val hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTES)
        // format iterations:salt:hash
        return PBKDF2_ITERATIONS.toString() + ":" + toHex(salt) + ":" + toHex(hash)
    }

    /**
     * Validates a password using a hash.

     * @param   password    the password to check
     * *
     * @param   goodHash    the hash of the valid password
     * *
     * @return              true if the password is correct, false if not
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun validatePassword(password: String, goodHash: String): Boolean {
        return validatePassword(password.toCharArray(), goodHash)
    }

    /**
     * Validates a password using a hash.

     * @param   password    the password to check
     * *
     * @param   goodHash    the hash of the valid password
     * *
     * @return              true if the password is correct, false if not
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun validatePassword(password: CharArray, goodHash: String): Boolean {
        // Decode the hash into its parameters
        val params = goodHash.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val iterations = Integer.parseInt(params[ITERATION_INDEX])
        val salt = fromHex(params[SALT_INDEX])
        val hash = fromHex(params[PBKDF2_INDEX])
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        val testHash = pbkdf2(password, salt, iterations, hash.size)
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash)
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.

     * @param   a       the first byte array
     * *
     * @param   b       the second byte array
     * *
     * @return          true if both byte arrays are the same, false if not
     */
    private fun slowEquals(a: ByteArray, b: ByteArray): Boolean {
        var diff = a.size xor b.size
        var i = 0
        while (i < a.size && i < b.size) {
            diff = diff or (a[i].toInt() xor b[i].toInt())
            i++
        }
        return diff == 0
    }

    /**
     * Computes the PBKDF2 hash of a password.

     * @param   password    the password to hash.
     * *
     * @param   salt        the salt
     * *
     * @param   iterations  the iteration count (slowness factor)
     * *
     * @param   bytes       the length of the hash to compute in bytes
     * *
     * @return              the PBDKF2 hash of the password
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int, bytes: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, bytes * 8)
        val skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        return skf.generateSecret(spec).encoded
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.

     * @param   hex         the hex string
     * *
     * @return              the hex string decoded into a byte array
     */
    private fun fromHex(hex: String): ByteArray {
        val binary = ByteArray(hex.length / 2)
        for (i in binary.indices) {
            binary[i] = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16).toByte()
        }
        return binary
    }

    /**
     * Converts a byte array into a hexadecimal string.

     * @param   array       the byte array to convert
     * *
     * @return              a length*2 character string encoding the byte array
     */
    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex
        else
            return hex
    }

    /**
     * Tests the basic functionality of the PasswordHash class

     * @param   args        ignored
     */
    @JvmStatic fun main(args: Array<String>) {
        try {
            // Print out 10 hashes
            for (i in 0..9)
                println(PasswordHash.createHash("p\r\nassw0Rd!"))

            // Test password validation
            var failure = false
            println("Running tests...")
            for (i in 0..99) {
                val password = "" + i
                val hash = createHash(password)
                val secondHash = createHash(password)
                if (hash == secondHash) {
                    println("FAILURE: TWO HASHES ARE EQUAL!")
                    failure = true
                }
                val wrongPassword = "" + (i + 1)
                if (validatePassword(wrongPassword, hash)) {
                    println("FAILURE: WRONG PASSWORD ACCEPTED!")
                    failure = true
                }
                if (!validatePassword(password, hash)) {
                    println("FAILURE: GOOD PASSWORD NOT ACCEPTED!")
                    failure = true
                }
            }
            if (failure)
                println("TESTS FAILED!")
            else
                println("TESTS PASSED!")
        } catch (ex: Exception) {
            println("ERROR: " + ex)
        }

    }

}