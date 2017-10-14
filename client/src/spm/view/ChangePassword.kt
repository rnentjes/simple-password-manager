package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import spm.view.input.TextInput

/**
 * User: rnentjes
 * Date: 5-4-17
 * Time: 9:46
 */

class ChangePassword : Komponent() {
    var currentPassword = ""
    var newPassword1 = ""
    var newPassword2 = ""

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-12") {

        form(classes = "form form-horizontal") {

            include(TextInput("current_password", "Current passphrase", "",
              labelWidth = 4,
              blur = { e ->
                  currentPassword = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  currentPassword = (e.target as HTMLInputElement).value
              }))
            include(TextInput("new_password1", "New passphrase", "",
              labelWidth = 4,
              blur = { e ->
                  newPassword1 = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  newPassword1 = (e.target as HTMLInputElement).value
              }))
            include(TextInput("new_password2", "Confirm new passphrase", "",
              labelWidth = 4,
              blur = { e ->
                  newPassword2 = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  newPassword2 = (e.target as HTMLInputElement).value
              }))

        }
    }

}
