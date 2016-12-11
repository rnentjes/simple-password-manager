package nl.astraeus.spm.model

import nl.astraeus.database.SimpleDao
import nl.astraeus.database.annotations.Cache
import nl.astraeus.database.annotations.Id
import nl.astraeus.database.annotations.IdType
import nl.astraeus.database.annotations.Table
import nl.astraeus.database.execute
import nl.astraeus.database.query
import java.util.*

/**
 * User: rnentjes
 * Date: 29-6-16
 * Time: 14:33
 */

@Table(name = "version")
@Cache(maxSize = 25)
data class Version(
  @Id(IdType.MANUAL) var number: Long = 0,
  var query: String,
  var executeDate: Date
) {
    constructor(): this(0, "", Date())
}

object VersionDao: SimpleDao<Version>(Version::class.java) {

    fun executeUpdate(number:Long, query: String) {
        execute(query = query)

        insert(Version(number, query, Date()))
    }

    fun findCurrentVersion(): Long {
        val rs = query(query = "SELECT MAX(number) FROM version")
        var result = -1L

        if (rs.next()) {
            result = rs.getLong(1)
        }

        return result
    }
}
