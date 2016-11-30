package spm.view.password

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.EventTarget
import spm.state.UserState
import spm.view.*
import spm.view.form.Form
import spm.view.form.FormType
import spm.view.form.IconButton
import spm.view.form.Input
import spm.view.group.GroupView
import spm.view.modal.ModalView
import spm.ws.Tokenizer
import spm.ws.WebSocketConnection
import kotlin.browser.window
import kotlin.dom.onClick
import kotlin.dom.removeClass

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

data class PasswordForm(
  var title: String = "",
  var url: String = "",
  var username: String = "",
  var password: String = "",
  var password2: String = ""
) {

    fun validate(): Boolean {
        elem("modal_confirm_button").removeAttribute("disabled")

        return true
    }

    fun save() {
        if (validate()) {
            WebSocketConnection.send("NEWPASSWORD", title, url, username, UserState.encryptPassword(password))
        }
    }
}

object PasswordOverviewView {
    var passwordForm = PasswordForm()

    fun show(passwords: List<Password>) {
        // passwords_overview
        clear("passwords_overview")
        val element = elem("passwords_overview")

        element.add {
            div().cls("row").txt("&nbsp")
        }.add {
            div().cls("row").add {
                div().cls("col-md-4").add {
                    createTag("h4").txt("Passwords")
                }
            }.add {
                div().cls("col-sm-8").add {
                    val a = createTag("a").cls("btn btn-success btn-sm").txt("Add")

                    a.onClick {
                        ModalView.showConfirm("New password",
                          body = getPasswordForm(),
                          denyText = "Cancel",
                          confirmText = "Save",
                          disabledConfirm = false,
                          confirm = { e ->
                              if (!savePasswordForm()) {
                                  e.preventDefault()
                              }
                          })
                    }

                    a
                }
            }
        }

        val table = createTag("table").cls("table table-striped table-condensed table-hover")

        table.add {
            createTag("tr")
              .add { createTag("th").txt("Title")   }
              .add { createTag("th").txt("Url")   }
              .add { createTag("th").txt("Notes")   }
              .add { createTag("th").txt("")   }
        }

        for (password in passwords) {
            table.add {
                createTag("tr")
                  .add { createTag("td").txt(password.title)   }
                  .add { createTag("td").txt(password.website)   }
                  .add { createTag("td").txt(password.username)   }
                  .add {
                      createTag("td").add {
                          IconButton.create("copy", text = "Username&nbsp;&nbsp;", buttonClass = "btn-xs btn-default") {
                              copyToClipboard(password.username)

                              ModalView.showAlert("Success!", "Copied username to clipboard!")
                          }
                      }.add {
                          IconButton.create("copy", text = "Password&nbsp;&nbsp;", buttonClass = "btn-xs btn-warning") {
                              copyToClipboard(UserState.decryptPassword(password.encryptedPassword))

                              ModalView.showAlert("Success!", "Copied password to clipboard!")
                          }.attr("style", "margin-left: 5px;")
                      }.add {
                          IconButton.create("folder-open", buttonClass = "btn-xs btn-success") {
                              window.alert("open password info!")
                          }.attr("style", "margin-left: 5px;")
                      }
                  }
            }
        }

        element.add {
            div().cls("row").add {
                table
            }
        }
    }

    private fun getPasswordForm(): Element {
        return Form.create(FormType.HORIZONTAL)
          .add {
            Input.create(
              "modal_password_title",
              label = "Title",
              labelWidth = 4,
              value = "") { e ->
                passwordForm.username = (e.target as HTMLInputElement).value
            }
        }.add {
            Input.create(
              "modal_password_url",
              label = "Url",
              labelWidth = 4,
              value = "") { e ->
                passwordForm.url = (e.target as HTMLInputElement).value
            }
        }.add {
            Input.create(
              "modal_password_username",
              label = "Username",
              labelWidth = 4,
              inputWidth = 7,
              value = "",
              change = { e ->
                  passwordForm.username = (e.target as HTMLInputElement).value
                  passwordForm.validate()
              }
            ).attr("id", "modal_password_username_warning")
        }.add {
            Input.create("modal_password_password1",
              type = "password",
              label = "Password",
              labelWidth = 4,
              inputWidth = 7,
              value = "",
              change = { e ->
                  passwordForm.password = (e.target as HTMLInputElement).value
                  checkForm()
              }
            ).attr("id", "modal_password_password1_warning")
              .attr("data-show-password", "false")
              .add {
                  IconButton.create("eye-open") { e ->
                      switchPasswordView(e.target)
                  }
              }
        }.add {
            Input.create(
              "modal_password_password2",
              type = "password",
              label = "Confirm password",
              labelWidth = 4,
              value = "",
              change = { e ->
                  passwordForm.password2 = (e.target as HTMLInputElement).value
                  checkForm()
              }
            )
        }.add {
            div()
              .cls("form-group")
              .add {
                div()
                  .attr("id", "modal_password_message")
                  .cls("col-sm-offset-4 col-sm-8 has-error")
            }
        }
    }

    private fun switchPasswordView(target: EventTarget?) {
/*
        val formGroup = target.parentElement
        var old: HTMLInputElement? = null

        if (formGroup != null) {
            val showPassword = formGroup.getAttribute("data-show-password") == "true"

            for (child in formGroup.childElements()) {
                if (child is HTMLInputElement) {
                    old = child
                }
                if (child is HTMLButtonElement) {
                    if (showPassword) {
                        child.removeClass("glyphicon-eye-open")
                        child.addClass("glyphicon-eye-closed")
                    } else {
                        child.removeClass("glyphicon-eye-closed")
                        child.addClass("glyphicon-eye-open")
                    }
                }
            }

            if (old != null) {
                //formGroup.removeChild(old)
            }
        }
*/
    }

    private fun checkForm(): Boolean {
        val username = elem("modal_password_username") as HTMLInputElement
        val password1 = elem("modal_password_password1") as HTMLInputElement
        val password2 = elem("modal_password_password2") as HTMLInputElement

        if (username.value.isEmpty()) {
            elem("modal_password_username_warning").cls("has-error")

        }

        elem("modal_password_message").txt("")
        elem("modal_password_username_warning").removeClass("has-error")
        //elem("modal_confirm_button").removeAttribute("disabled")

        return true
    }

    private fun savePasswordForm(): Boolean {
        val groupId = UserState.currentGroup?.id ?: throw IllegalStateException("No current group selected!?")

        val title = elem("modal_password_title") as HTMLInputElement
        val url = elem("modal_password_url") as HTMLInputElement
        val username = elem("modal_password_username") as HTMLInputElement

        val password1 = elem("modal_password_password1") as HTMLInputElement
        val password2 = elem("modal_password_password2") as HTMLInputElement

        if (password1.value != password2.value) {
            ModalView.showAlert("Error", "Passwords do not match!")
            return true
        } else {
            if (checkForm()) {
                WebSocketConnection.send("NEWPASSWORD", "$groupId", title.value, url.value, username.value, UserState.encryptPassword(password1.value))
                return true
            } else {
                return false
            }
        }
    }

}
