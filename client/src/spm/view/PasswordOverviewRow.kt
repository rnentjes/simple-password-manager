package spm.view

import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.title
import kotlinx.html.tr
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import spm.model.Password
import spm.state.UserState
import spm.util.copyToClipboard
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
  val password: Password,
  val container: Komponent,
  val showGroup: Boolean = false
) : Komponent() {

    fun trimmed(consumer: TagConsumer<HTMLElement>, str: String, length: Int) = consumer.td {
        if (str.length > length) {
            title = str
            + "${str.substring(0 until length - 3)}..."
        } else {
            + str
        }
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.tr {

        if (showGroup) {
            trimmed(consumer, password.group.name, 8)
        }
        trimmed(consumer, password.title, 12)
        trimmed(consumer, password.website, 24)
        trimmed(consumer, password.username, 12)
        td(classes = "col-md-4 nowrap") {
            include(PasswordButton(
              "copy",
              text = "U ",
              tooltip = "Copy username",
              btnClass = "btn-xs btn-default"
            ) {
                copyToClipboard(password.username)

                Notify.show("Copied username to clipboard.", "success")
            })
            include(PasswordButton(
              "copy",
              text = "P ",
              tooltip = "Copy password",
              btnClass = "btn-xs btn-warning",
              buttonStyle = "margin-left: 5px;"
            ) {
                copyToClipboard(UserState.decryptPassword(password.encryptedPassword))

                Notify.show("Copied password to clipboard.", "success")
            })
            include(PasswordButton(
              "copy",
              text = "U ",
              tooltip = "Copy url",
              btnClass = "btn-xs btn-default",
              buttonStyle = "margin-left: 5px;") {
                copyToClipboard(password.website)

                Notify.show("Copied password to clipboard.", "success")
            })
            include(PasswordButton(
              "new-window",
              text = "U ",
              tooltip = "Open url in a new window",
              btnClass = "btn-xs btn-default",
              buttonStyle = "margin-left: 5px;") {
                window.open(password.website, "_blank")
            })
            include(PasswordButton(
              "folder-open",
              tooltip = "Edit password entry",
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

                                val originalGroup = editor.originalPassword.group
                                val newGroup = editor.password.group

                                originalGroup.passwords.remove(editor.originalPassword)
                                newGroup.passwords.add(editor.originalPassword)
                                editor.originalPassword.group = editor.password.group

                                if (editor.password.password1.isNotBlank()) {
                                    val oldPassword = UserState.decryptPassword(editor.originalPassword.encryptedPassword)
                                    if (oldPassword != editor.password.password1) {
                                        editor.originalPassword.archivePassword()
                                        editor.originalPassword.encryptedPassword = UserState.encryptPassword(editor.password.password1)
                                    }
                                } else {
                                    //console.log("blank pwd: ", password)
                                }
                            } else {
                                throw IllegalStateException("Edit button doesn't have original password!?")
                            }

                            UserState.saveData()
                            container.refresh()

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
                  tooltip = "Remove password entry",
                  btnClass = "btn-xs btn-danger",
                  buttonStyle = "margin-left: 5px;") {
                    Modal.openModal("Remove password",
                      RemovePasswordConfirm(password),
                      okButtonClass = "btn-danger",
                      ok = {
                          password.delete()
                          UserState.saveData()
                          container.refresh()

                          true
                      })
                })
            }
        }
    }

}

