package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import spm.view.table.Table
import spm.ws.WebSocketConnection
import kotlin.browser.document

/**
 * Created by rnentjes on 4-4-17.
 */

class RemovePasswordConfirm(val password: Password) : Komponent() {
  override fun render(consumer: HtmlBuilder) = consumer.span {
    +"Are you sure you want to remove password '${password.title}'?"
  }
}

class RemoveGroupConfirm(val groupName: String) : Komponent() {
  override fun render(consumer: HtmlBuilder) = consumer.span {
    +"Are you sure you want to remove group '$groupName'?"
  }
}

class GroupNameEdit(var groupname: String = "") : Komponent() {

  override fun render(consumer: HtmlBuilder) = consumer.div(classes = "") {
    form(classes = "form form-horizontal") {
      div(classes = "form-group") {
        label(classes = "col-md-3") {
          for_ = "groupname"
          +"Group name"
        }
        div(classes = "col-md-9") {
          input(classes = "form-control") {
            id = "groupname"
            value = groupname

            fun changeName(e: Event) {
              e.preventDefault()

              groupname = (e.target as HTMLInputElement).value
            }

            onBlurFunction = ::changeName
            onKeyUpFunction = ::changeName
          }
        }
      }

    }
  }
}

class PasswordOverview(val container: Komponent) : Komponent() {

  fun addPassword(cg: Group) {

    WebSocketConnection.lock { ws, tk ->
      val response = tk.next()

      if (response == "LOCKED") {
        val editor = PasswordEditor(cg)
        editor.password.group = cg
        Modal.openModal(
            "Edit password",
            editor,
            okText = "Save",
            okButtonClass = "btn-success",
            ok = {
              if (editor.validate()) {
                if (editor.originalPassword == null) {
                  editor.password.encryptedPassword = UserState.encryptPassword(editor.password.password1)
                  editor.password.group.passwords.add(editor.password)
                } else {
                  throw IllegalStateException("Add button modal has existing password!?")
                }
                UserState.saveData()
                container.refresh()

                true
              } else {

                false
              }
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

  override fun render(consumer: HtmlBuilder) = consumer.div(classes = "col-md-9") {
    val cg = UserState.currentGroup
    //console.log("Currentgroup: ", cg)
    if (cg != null) {
      div(classes = "row") {
        div(classes = "col-md-6") {
          h3 {
            // background-color: #f8f8f8;
            style = "text-align: center; padding: 10px; margin: 5px"
            val group = UserState.currentGroup
            if (group != null) {
              +group.name
            }
          }
        }

        if (!UserState.readOnly) {
          include(GroupCommands(cg, container))
        }
      }
      hr {}
      div {
        //id = "passwords_overview"
        div(classes = "page-header") {
          div(classes = "btn-toolbar pull-right") {
            div(classes = "button-group") {
              if (!UserState.readOnly) {
                a(classes = "btn btn-success btn-sm") {
                  +"Add"
                  onClickFunction = {
                    this@PasswordOverview.addPassword(cg)
                  }
                }
              }
            }
          }
          h4 {
            +"Passwords"
          }
        }
        div(classes = "row") {
          div(classes = "col-md-12") {
            passwordTable(cg.passwords, container)
          }
        }
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
