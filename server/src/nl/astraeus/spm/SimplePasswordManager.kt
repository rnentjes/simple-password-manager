package nl.astraeus.spm

import nl.astraeus.database.SimpleDatabase
import nl.astraeus.database.jdbc.ConnectionPool
import nl.astraeus.database.jdbc.ConnectionProvider
import nl.astraeus.spm.model.LockDao
import nl.astraeus.spm.sql.DatabaseMigration
import nl.astraeus.spm.web.SimpleWebSocketServer
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:09
 */

object Settings {
    var port                = 3456
    var connectionTimeout   = 30000

    var jdbcDriver          = "org.h2.Driver" // "nl.astraeus.jdbc.Driver"mail
    var jdbcConnectionUrl   = "jdbc:h2:file:" // "jdbc:stats::jdbc:h2:file:"
    var jdbcUser            = "sa"
    var jdbcPassword        = ""

    init {
        val file = File("data", "spm")

        jdbcConnectionUrl += file.canonicalPath
    }

    fun readProperties(args: Array<String>) {
        var filename = "spm.properties"

        if (args.isNotEmpty()) {
            filename = args[0]
        }

        val propertiesFile = File(filename)

        if (propertiesFile.exists()) {
            val properties = Properties()

            FileInputStream(propertiesFile).use {
                properties.load(it)
            }

            port = properties.getProperty("port", port.toString()).toInt()
            connectionTimeout = properties.getProperty("connectionTimeout", connectionTimeout.toString()).toInt()

            jdbcDriver = properties.getProperty("jdbcDriver", jdbcDriver)
            jdbcConnectionUrl = properties.getProperty("jdbcConnectionUrl", jdbcConnectionUrl)
            jdbcUser = properties.getProperty("jdbcUser", jdbcUser)
            jdbcPassword = properties.getProperty("jdbcPassword", jdbcPassword)

        }
    }
}

fun initDbConnection() {
    val db = SimpleDatabase.define(ConnectionPool(object : ConnectionProvider() {
        override fun getConnection(): Connection {
            Class.forName(Settings.jdbcDriver)

            val connection = DriverManager.getConnection(
              Settings.jdbcConnectionUrl,
              Settings.jdbcUser,
              Settings.jdbcPassword)
            connection.autoCommit = false

            return connection
        }
    }))

    db.setExecuteDDLUpdates(true)
}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("nl.astraeus.spm.main")

    Settings.readProperties(args)

    val server = SimpleWebSocketServer(Settings.port)

    initDbConnection()
    DatabaseMigration.check()

    LockDao.emptyRows()

    logger.info("Server should start in client directory.")
    logger.info("Starting server in directory: ${File(".").canonicalPath}")
    logger.info("Starting server on port: ${Settings.port}")

    server.start(Settings.connectionTimeout, false)
}
