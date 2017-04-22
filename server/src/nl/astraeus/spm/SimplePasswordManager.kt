package nl.astraeus.spm

import nl.astraeus.database.SimpleDatabase
import nl.astraeus.database.jdbc.ConnectionPool
import nl.astraeus.database.jdbc.ConnectionProvider
import nl.astraeus.spm.sql.DatabaseMigration
import nl.astraeus.spm.web.SimpleWebSocketServer
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:09
 */

fun initDbConnection(connectionString: String) {
    val db = SimpleDatabase.define(ConnectionPool(object : ConnectionProvider() {
        override fun getConnection(): Connection {
            Class.forName("org.h2.Driver")
            Class.forName("nl.astraeus.jdbc.Driver")

            val connection = DriverManager.getConnection(connectionString, "sa", "")
            connection.autoCommit = false

            return connection
        }

    }))

    db.setExecuteDDLUpdates(true)
}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("nl.astraeus.spm.main")

    val server = SimpleWebSocketServer(3456)

    val cs = "jdbc:stat:webServerPort=18200:jdbc:h2:file:/home/rnentjes/apps/spm/data/data"

    initDbConnection(cs)
    DatabaseMigration.check()

    server.start(30000, false)

    logger.warn("Started server!")
}
