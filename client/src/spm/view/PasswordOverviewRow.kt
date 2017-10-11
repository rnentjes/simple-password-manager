package spm.view

import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.tr
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import spm.state.UserState
import spm.model.Password
import spm.view.button.PasswordButton
import stats.view.Modal
import kotlin.browser.document
import kotlin.browser.window

/**
 * User: rnentjes
 * Date: 11-10-17
 * Time: 16:48
 */

class PasswordOverviewRow(
  val password: Password
) : Komponent() {
    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.tr {

        td { +password.group.name }
        td { +password.title }
        td { +password.website }
        td { +password.username }
        td(classes = "col-md-4") {
            include(PasswordButton(
              "copy",
              text = "U ",
              btnClass = "btn-xs btn-default"
            ) {
                copyToClipboard(password.username)

                Notify.show("Copied username to clipboard.", "success")
            })
            include(PasswordButton(
              "copy",
              text = "P ",
              btnClass = "btn-xs btn-warning",
              buttonStyle = "margin-left: 5px;"
            ) {
                copyToClipboard(UserState.decryptPassword(password.encryptedPassword))

                Notify.show("Copied password to clipboard.", "success")
            })
            include(PasswordButton(
              "copy",
              text = "U ",
              btnClass = "btn-xs btn-default",
              buttonStyle = "margin-left: 5px;") {
                copyToClipboard(password.website)

                Notify.show("Copied password to clipboard.", "success")
            })
            include(PasswordButton(
              "new-window",
              text = "U ",
              btnClass = "btn-xs btn-default",
              buttonStyle = "margin-left: 5px;") {
                window.open(password.website, "_blank")
            })
            include(PasswordButton(
              "folder-open",
              btnClass = "btn-xs btn-success",
              buttonStyle = "margin-left: 5px;") {
                val editor = PasswordEditor(password.group, password)
                Modal.openModal("Edit password", editor, /*modalSize = "modal-lg", */ok = {
                    if (!UserState.readOnly) {
                        if (editor.validate()) {
                            if (editor.originalPassword != null) {
                                editor.originalPassword.title = editor.password.title
                                editor.originalPassword.website = editor.password.website
                                editor.originalPassword.username = editor.password.username
                                editor.originalPassword.description = editor.password.description

                                if (editor.password.password1.isNotBlank()) {
                                    editor.originalPassword.encryptedPassword = UserState.encryptPassword(editor.password.password1)
                                } else {
                                    //console.log("blank pwd: ", password)
                                }
                            } else {
                                throw IllegalStateException("Edit button doesn't have original password!?")
                            }

                            UserState.saveData()
                            refresh()

                            true
                        } else {
                            false
                        }
                    } else {
                        true
                    }
                })
            })
            if (!UserState.readOnly) {
                include(PasswordButton(
                  "remove",
                  btnClass = "btn-xs btn-danger",
                  buttonStyle = "margin-left: 5px;") {
                    Modal.openModal("Remove password",
                      RemovePasswordConfirm(password),
                      okButtonClass = "btn-danger",
                      ok = {
                          password.delete()
                          UserState.saveData()
                          refresh()

                          true
                      })
                })
            }
        }
    }

    fun copyToClipboard(text: String) {
        val ta = document.createElement("textarea")
        ta.innerHTML = text

        if (ta is HTMLTextAreaElement) {
            val body = document.body ?: throw IllegalStateException("The body was not found!")

            body.appendChild(ta)
            ta.select()
            document.execCommand("copy")
            body.removeChild(ta)
        }
    }

}

