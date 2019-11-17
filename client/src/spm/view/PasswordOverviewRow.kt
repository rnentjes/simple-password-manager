package spm.view

import kotlinx.html.Tag
import kotlinx.html.td
import kotlinx.html.tr
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import spm.model.Password
import spm.state.UserState
import spm.util.copyToClipboard
import spm.util.formatted
import spm.util.trimmed
import spm.view.button.PasswordButton
import spm.view.table.Table
import spm.ws.WebSocketConnection
import kotlin.browser.window
import kotlin.js.Date

/**
 * User: rnentjes
 * Date: 11-10-17
 * Time: 16:48
 */


fun Tag.passwordTable(
    passwords: List<Password>,
    container: Komponent,
    showGroup: Boolean = false
) {
  include(Table(
      if (showGroup) {
        arrayOf(
            "Group",
            "Title",
            "Url",
            "Username",
            "Hist",
            ""
        )
      } else {
        arrayOf(
            "Title",
            "Url",
            "Username",
            "Hist",
            ""
        )
      },
      passwords
  ) { password ->
    include(PasswordOverviewRow(password, container, showGroup))
  })
}

class PasswordOverviewRow(
    val password: Password,
    val container: Komponent,
    val showGroup: Boolean = false
) : Komponent() {

  init {
    style("nowrap", Styles.nowrap)
  }

  private fun editPassword(password: Password) {
    if (UserState.readOnly) {
      openEditPasswordModal(password)
    } else {
      WebSocketConnection.lock { ws, tk ->
        val response = tk.next()

        if (response == "LOCKED") {
          openEditPasswordModal(password, true)
        } else {
          Modal.showAlert("Blocked", "Unable to obtain modify lock.")
        }
      }
    }
  }

  private fun openEditPasswordModal(password: Password, locked: Boolean = false) {
    val editor = PasswordEditor(password.group, password)
    Modal.openModal(
        "Edit password",
        editor,
        /*modalSize = "modal-lg", */
        ok = {
          if (!UserState.readOnly) {
            if (editor.validate()) {
              if (editor.originalPassword != null) {
                editor.originalPassword.title = editor.password.title
                editor.originalPassword.website = editor.password.website
                editor.originalPassword.username = editor.password.username
                editor.originalPassword.description = editor.password.description

                val originalGroup = editor.originalPassword.group
                val newGroup = editor.password.group

                if (originalGroup != newGroup) {
                  originalGroup.passwords.remove(editor.originalPassword)
                  newGroup.passwords.add(editor.originalPassword)
                  editor.originalPassword.group = editor.password.group
                }

                if (editor.password.password1.isNotBlank()) {
                  val oldPassword = UserState.decryptPassword(
                      editor.originalPassword.encryptedPassword
                  )
                  if (oldPassword != editor.password.password1) {
                    editor.originalPassword.archivePassword()
                    editor.originalPassword.encryptedPassword = UserState.encryptPassword(
                        editor.password.password1
                    )
                    editor.originalPassword.created = Date().formatted()
                  }
                } else {
                  //console.log("blank pwd: ", password)
                }
              } else {
                throw IllegalStateException("Edit button doesn't have original password!?")
              }

              if (locked) {
                UserState.saveData()
                container.update()
              }

              true
            } else {
              false
            }
          } else {
            true
          }
        },
        cancel = if (locked) {
          { WebSocketConnection.send("UNLOCK") }
        } else {
          {}
        }
    )
  }

  private fun removePassword(password: Password) {
    WebSocketConnection.lock { ws, tk ->
      val response = tk.next()

      if (response == "LOCKED") {
        Modal.openModal(
            "Remove password",
            RemovePasswordConfirm(password),
            okButtonClass = "btn-danger",
            ok = {
              password.delete()
              UserState.saveData()
              container.update()

              true
            },
            cancel = {
              ws.send("UNLOCK")
            }
        )
      } else {
        Modal.showAlert("Blocked", "Unable to obtain modify lock.")
      }
    }
  }

  override fun render(consumer: HtmlBuilder) = consumer.tr {

    if (showGroup) {
      trimmed(password.group.name, 8)
    }
    trimmed(password.title, 12)
    trimmed(password.website, 24)
    trimmed(password.username, 12)

    td {
      if (password.history.isNotEmpty()) {
        include(PasswordButton(
            "",
            text = "${password.history.size} ",
            tooltip = "Clear history",
            btnClass = "btn-xs btn-danger"
        ) {
          Modal.openModal(
              "Remove password",
              ClearHistoryConfirm(),
              okButtonClass = "btn-danger",
              ok = {
                password.history.clear()
                UserState.saveData()
                update()

                true
              }
          )
        })
      }
    }
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
          buttonStyle = "margin-left: 5px;"
      ) {
        copyToClipboard(password.website)

        Notify.show("Copied password to clipboard.", "success")
      })
      include(PasswordButton(
          "new-window",
          text = "U ",
          tooltip = "Open url in a new window",
          btnClass = "btn-xs btn-default",
          buttonStyle = "margin-left: 5px;"
      ) {
        window.open(password.website, "_blank")
      })
      include(PasswordButton(
          "folder-open",
          tooltip = "Edit password entry",
          btnClass = "btn-xs btn-success",
          buttonStyle = "margin-left: 5px;"
      ) {
        editPassword(password)
      })
      if (!UserState.readOnly) {
        include(PasswordButton(
            "remove",
            tooltip = "Remove password entry",
            btnClass = "btn-xs btn-danger",
            buttonStyle = "margin-left: 5px;"
        ) {
          removePassword(password)
        })
      }
    }
  }

}

