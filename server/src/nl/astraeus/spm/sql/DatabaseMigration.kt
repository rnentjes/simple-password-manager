package nl.astraeus.spm.sql

import nl.astraeus.database.transaction
import nl.astraeus.spm.model.Version
import nl.astraeus.spm.model.VersionDao
import java.util.*

/**
 * User: rnentjes
 * Date: 29-6-16
 * Time: 14:31
 */

class FuncWrap(
  val description: String,
  val func: () -> Unit
)

object DatabaseMigration {

    val tasks: Array<Any> = arrayOf(
      ""
    )

    fun check() {
        var currentVersion = -1

        transaction {
            currentVersion = VersionDao.findCurrentVersion().toInt()
            println("Current database version $currentVersion")
        }

        for (index in (currentVersion + 1)..(tasks.size - 1)) {
            transaction {
                println("Executing db update $index")
                val task = tasks[index]
                if (task is String) {
                    println("\t$task")
                    VersionDao.executeUpdate(index.toLong(), task)
                } else if (task is FuncWrap) {
                    task.func()

                    VersionDao.insert(Version(index.toLong(), task.description, Date()))
                }
            }
        }
    }

}
