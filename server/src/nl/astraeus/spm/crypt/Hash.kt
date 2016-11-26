package nl.astraeus.spm.crypt

import java.security.MessageDigest



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