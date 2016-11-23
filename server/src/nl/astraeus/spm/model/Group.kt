package nl.astraeus.spm.model

import nl.astraeus.database.Dao
import nl.astraeus.database.annotations.*
import nl.astraeus.spm.util.DateFormatter
import nl.astraeus.spm.ws.Tokenizer
import java.text.SimpleDateFormat
import java.util.*

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 13:01
 */

@Table(name = "groups")
@Cache(maxSize = 5000)
data class Group(
  @Id(IdType.AUTOGENERATED) var id: Long = 0,
  @Index var user: String,
  @Index var parent: String,
  var name: String,

  var created: Date,
  var updated: Date
) {
    constructor(): this(0, "", "", "", Date(), Date())

    constructor(tk: Tokenizer): this(
      tk.next().toLong(),
      tk.next(),
      tk.next(),
      tk.next(),
      Date(),
      Date()
      ) {
        val formatter = DateFormatter.get()

        created = formatter.parse(tk.next())
        updated = formatter.parse(tk.next())
    }

    fun tokenized(): String {
        val formatter = DateFormatter.get()

        return Tokenizer.tokenize("$id", user, parent, name, formatter.format(created), formatter.format(updated))
    }
}

object GroupDao: Dao<Group>(Group::class.java) {

    fun findByUser(email: String) = where("user = ?", email)

}

fun main(args: Array<String>) {
    val group = Group(0, "name", "parent", "name", Date(), Date())

    val tokenized = group.tokenized()

    println("Tokenized: $tokenized")

    val tokenizer = Tokenizer(tokenized)

    println("Parsed: ${Group(tokenizer)}")
}