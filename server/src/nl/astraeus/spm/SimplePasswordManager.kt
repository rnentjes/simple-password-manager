package nl.astraeus.spm

import nl.astraeus.database.DdlMapping
import nl.astraeus.database.setConnectionProvider
import nl.astraeus.database.transaction
import nl.astraeus.spm.model.User
import nl.astraeus.spm.model.UserDao
import nl.astraeus.spm.sql.DatabaseMigration
import nl.astraeus.spm.web.SimpleWebSocketServer
import org.slf4j.LoggerFactory
import java.sql.DriverManager
import java.util.*

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:09
 */

fun initDbConnection(connectionString: String) {
    DdlMapping.get().setExecuteDDLUpdates(true)

    setConnectionProvider {
        Class.forName("org.h2.Driver")
        Class.forName("nl.astraeus.jdbc.Driver")

        val connection = DriverManager.getConnection(connectionString, "sa", "")
        connection.autoCommit = false

        // result
        connection
    }
}

fun checkAdminUser() {
    transaction {
        if (UserDao.findByEmail("admin@astraeus.nl") == null) {
            val user = User(0, "admin@astraeus.nl", "admin", "Rien", Date(), Date())

            UserDao.insert(user)
        }
    }
}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("nl.astraeus.daw.Main")

    val server = SimpleWebSocketServer(3456)

    val cs = "jdbc:stat:webServerPort=18100:jdbc:h2:file:/home/rnentjes/apps/spm/data/data"

    initDbConnection(cs)
    checkAdminUser()
    DatabaseMigration.check()

    server.start(11000, false)

    logger.warn("Started server!")
}
