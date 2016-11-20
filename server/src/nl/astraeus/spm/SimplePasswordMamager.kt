package nl.astraeus.spm

import nl.astraeus.database.DdlMapping
import nl.astraeus.database.setConnectionProvider
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.slf4j.LoggerFactory
import java.sql.DriverManager

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

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("nl.astraeus.daw.Main")

    val server = Server()
    val connector = ServerConnector(server)
    connector.idleTimeout = 1000
    connector.acceptQueueSize = 10
    connector.port = 3456
    connector.host = "0.0.0.0"

    val servletContext = ServletContextHandler(server, "", true, false)

//    servletContext.addServlet(ServletHolder("websocket", SimpleWebSocketServlet::class.java), "/daw")
//    servletContext.addServlet(ServletHolder("audio", AudioServlet::class.java), "/play")
    servletContext.addServlet(ServletHolder("info", InfoServlet::class.java), "/info")

    server.addConnector(connector)

    val cs = "jdbc:stat:webServerPort=18100:jdbc:h2:file:/home/rnentjes/apps/spm/data/data"

    initDbConnection(cs)
//    checkAdminUser()
//    DatabaseMigration.check()

    //server.dump(System.out)
    server.start()

    logger.warn("Started server!")
}
