package nl.astraeus.spm.util

import java.text.SimpleDateFormat

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 12:01
 */

object DateFormatter {

    fun get(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    }

}