package spm.view.password

import spm.view.*
import spm.ws.Tokenizer

/**
 * User: rnentjes
 * Date: 27-11-16
 * Time: 16:20
 */

data class Password(
  var id: Long,
  var user: String,
  var group: Long,
  var title: String,
  var website: String,
  var username: String,
  var encryptedPassword: String,
  var description: String
) {
    constructor(): this(0, "", 0, "", "", "", "", "")

    constructor(tk: Tokenizer): this(
      parseInt(tk.next()).toLong(),
      tk.next(),
      parseInt(tk.next()).toLong(),
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next())

    fun tokenized(): String = Tokenizer.tokenize("$id", user, "$group", title, website, username, encryptedPassword, description)
}

object PasswordOverviewView {

    fun show(passwords: List<Password>) {
        // passwords_overview
        clear("passwords_overview")
        val element = elem("passwords_overview")

        element.add { createTag("h4").txt("Passwords")}

        val table = createTag("table").cls("table")

        table.add {
            createTag("tr")
              .add { createTag("th").txt("Title")   }
              .add { createTag("th").txt("Url")   }
              .add { createTag("th").txt("Notes")   }
              .add { createTag("th").txt("&nbsp;")   }
        }

        for (password in passwords) {
            table.add {
                createTag("tr")
                  .add { createTag("td").txt(password.title)   }
                  .add { createTag("td").txt(password.website)   }
                  .add { createTag("td").txt(password.username)   }
                  .add { createTag("td").txt("&nbsp;")   }
            }
        }

        element.add { table }
    }

}
