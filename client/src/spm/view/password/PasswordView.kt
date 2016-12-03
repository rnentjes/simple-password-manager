package spm.view.password

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.EventTarget
import spm.state.UserState
import spm.view.*
import spm.view.form.*
import spm.view.group.Group
import spm.view.group.GroupView
import spm.view.modal.ModalView
import spm.ws.Tokenizer
import spm.ws.WebSocketConnection
import java.util.*
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
  var password1: String = "",
  var password2: String = "",
  var description: String
) {
    constructor() : this(0, "", 0, "", "", "", "", "", "", "")

    constructor(group: Group) : this(0, "", group.id, "", "", "", "", "", "", "")

    constructor(tk: Tokenizer) : this(
      parseInt(tk.next()).toLong(),
      tk.next(),
      parseInt(tk.next()).toLong(),
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next(),
      "",
      "",
      tk.next())

    fun tokenized(): String = Tokenizer.tokenize("$id", user, "$group", title, website, username, encryptedPassword, description)

    fun decrypt() {
        password1 = UserState.decryptPassword(encryptedPassword)
        password2 = password1
    }

    fun send() {
        if (password1.isNotBlank()) {
            encryptedPassword = UserState.encryptPassword(password1)
        }
        if (id == 0L) {
            WebSocketConnection.send("NEWPASSWORD", "$group", title, website, username, encryptedPassword, description)
        } else {
            WebSocketConnection.send("SAVEPASSWORD", "$group", "$id", title, website, username, encryptedPassword, description)
        }
        password1 = ""
        password2 = ""
    }

    fun delete() {
        WebSocketConnection.send("DELETEPASSWORD", "$group", "$id")
    }
}

class PasswordForm(
  val password: Password,
  var showPassword: Boolean = false,
  val messages: MutableMap<String, MutableList<String>> = HashMap(),
  var valid: Boolean = false
) {

    fun validate(): Boolean {
        valid = true
        messages.clear()

        check(password.title.isNotBlank(), "title", "Please enter a title for this password.")
        check(password.id > 0 || password.password1.isNotBlank(), "password1", "Password field can not be empty for a new entry.")
        check(password.password1 == password.password2, "password1", "Passwords don't match.")
        //check(password.password1.length >= 12, "password1", "Password is to short (at least 12 characters!)")

        println("validate: $valid -> $messages")
        return valid
    }

    fun check(expression: Boolean, param: String, message: String) {
        if (!expression) {
            valid = false
            addMessage(param, message)
        }
    }

    fun addMessage(param: String, message: String) {
        val list = messages[param] ?: ArrayList<String>()

        list.add(message)

        messages[param] = list
    }

    fun save() {
        if (validate()) {
            password.send()
        } else {
            ModalView.showAlert("Error", "Couldn't validate form?")
        }
    }

}

object PasswordOverviewView {

    fun show(parent: Element, group: Long, passwords: List<Password>) {
        // passwords_overview
        clear(parent)

        parent.add {
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
                        val password = Password()
                        val passwordForm = PasswordForm(password)

                        password.group = group

                        createPasswordEditor(parent, group, passwords, passwordForm)
                    }

                    a
                }
            }
        }

        val table = createTag("table").cls("table table-striped table-condensed table-hover")

        table.add {
            createTag("tr")
              .add { createTag("th").txt("Title") }
              .add { createTag("th").txt("Url") }
              .add { createTag("th").txt("Username") }
              .add { createTag("th").txt("Notes") }
              .add { createTag("th").txt("") }
              .add { createTag("th").txt("") }
              .add { createTag("th").txt("") }
              .add { createTag("th").txt("") }
        }

        for (password in passwords) {
            val passwordForm = PasswordForm(password)

            table.add {
                createTag("tr")
                  .add { createTag("td").txt(password.title) }
                  .add { createTag("td").txt(password.website) }
                  .add { createTag("td").txt(password.username) }
                  .add { createTag("td").txt(password.description) }
                  .add {
                      createTag("td").cls("col-md-5").add {
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
                              createPasswordEditor(parent, group, passwords, passwordForm)
                          }.attr("style", "margin-left: 5px;")
                      }.add {
                          IconButton.create("remove", buttonClass = "btn-xs btn-danger") {
                              ModalView.showConfirm(
                                "Delete password",
                                createTag("span").txt("Are you sure you want to delete password '${password.title}'?"),
                                confirm = {
                                    password.delete()
                                }
                              )
                          }.attr("style", "margin-left: 5px;")
                      }
                  }
            }
        }

        parent.add {
            div().cls("row").add {
                table
            }
        }
    }

    fun createPasswordEditor(
      parent: Element,
      group: Long,
      passwords: List<Password>,
      passwordForm: PasswordForm) {

        PasswordView.create(parent, passwordForm, cancel = {
            show(parent, group, passwords)
        }, save = {
            if (passwordForm.validate()) {
                passwordForm.save()

                show(parent, group, passwords)
            } else {
                createPasswordEditor(parent, group, passwords, passwordForm)
            }
        })
    }

}

object PasswordView {

    fun create(
      parent: Element,
      passwordForm: PasswordForm,
      cancel: (PasswordForm) -> Unit = {},
      save: (PasswordForm) -> Unit = {}
    ) {
        clear(parent)

        val password = passwordForm.password
        password.decrypt()
        var passwordType = "password"

        if (passwordForm.showPassword) {
            passwordType = "text"
        }

        parent.add {
            Form.create(FormType.HORIZONTAL)
              .add {
                  Input.create(
                    "modal_password_title",
                    label = "Title",
                    labelWidth = 4,
                    messages = passwordForm.messages["title"],
                    value = password.title) { e ->
                      password.title = (e.target as HTMLInputElement).value
                  }
              }.add {
                Input.create(
                  "modal_password_url",
                  label = "Url",
                  labelWidth = 4,
                  messages = passwordForm.messages["website"],
                  value = password.website) { e ->
                    password.website = (e.target as HTMLInputElement).value
                }
            }.add {
                Input.create(
                  "modal_password_username",
                  label = "Username",
                  labelWidth = 4,
                  inputWidth = 8,
                  messages = passwordForm.messages["username"],
                  value = password.username,
                  change = { e ->
                      password.username = (e.target as HTMLInputElement).value
                  }
                ).attr("id", "modal_password_username_warning")
            }.add {
                Input.create("modal_password_password1",
                  type = passwordType,
                  label = "Password",
                  labelWidth = 4,
                  inputWidth = 7,
                  value = password.password1,
                  messages = passwordForm.messages["password1"],
                  change = { e ->
                      password.password1 = (e.target as HTMLInputElement).value
                  }
                ).add {
                    if (passwordForm.showPassword) {
                        IconButton.create("eye-close") { e ->
                            passwordForm.showPassword = false
                            create(parent, passwordForm, cancel, save)
                        }
                    } else {
                        IconButton.create("eye-open") { e ->
                            passwordForm.showPassword = true
                            create(parent, passwordForm, cancel, save)
                        }
                    }
                }
            }.add {
                Input.create(
                  "modal_password_password2",
                  type = passwordType,
                  label = "Confirm password",
                  labelWidth = 4,
                  inputWidth = 7,
                  value = password.password2,
                  messages = passwordForm.messages["password2"],
                  change = { e ->
                      password.password2 = (e.target as HTMLInputElement).value
                  }
                ).add {
                    IconButton.create("cog") { e ->
                        //generate password options
                    }
                }
            }.add {
                TextArea.create(
                  "modal_password_notes",
                  label = "Notes",
                  labelWidth = 4,
                  inputWidth = 8,
                  messages = passwordForm.messages["description"],
                  value = password.description,
                  change = { e ->
                      password.description = (e.target as HTMLTextAreaElement).value
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
            }.add {
                div()
                  .cls("form-group")
                  .add {
                      div().cls("col-sm-offset-4 col-sm-2").add {
                          val saveButton = createTag("button").cls("btn btn-success").txt("Save")

                          saveButton.onClick { save(passwordForm) }

                          saveButton
                      }
                  }.add {
                    div().cls("col-sm-2").add {
                        val cancelButton = createTag("button").cls("btn btn-default").txt("Cancel")

                        cancelButton.onClick { cancel(passwordForm) }

                        cancelButton
                    }
                }
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
}