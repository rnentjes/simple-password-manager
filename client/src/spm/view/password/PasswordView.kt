package spm.view.password

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.EventTarget
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import spm.view.*
import spm.view.form.*
import spm.view.modal.ModalView
import spm.view.modal.Notify
import java.util.*
import kotlin.dom.onClick
import kotlin.dom.removeClass

/**
 * User: rnentjes
 * Date: 27-11-16
 * Time: 16:20
 */

class PasswordForm(
  val password: Password,
  var showPassword: Boolean = false,
  val messages: MutableMap<String, MutableList<String>> = HashMap(),
  var valid: Boolean = false
) {

    fun validate(newPassword: Boolean): Boolean {
        valid = true
        messages.clear()

        check(password.title.isNotBlank(), "title", "Please enter a title for this password.")
        check(!newPassword || password.password1.isNotBlank(), "password1", "Password field can not be empty for a new entry.")
        check(password.password1 == password.password2, "password1", "Passwords don't match.")

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

    fun save(newPassword: Boolean) {
        if (validate(newPassword)) {
            if (newPassword) {
                password.group.passwords.add(password)
            }

            UserState.saveData()
        } else {
            ModalView.showAlert("Error", "Couldn't validate form?")
        }
    }
}

class PasswordSettings(
  var length: Int = 32,
  var numbers: Boolean = true,
  var special: Boolean = true
)

object PasswordOverviewView {

    fun show(parent: Element, group: Group, passwords: List<Password>) {
        // passwords_overview
        clear(parent)

        parent.add {
            div().cls("page-header").add {
                div().cls("btn-toolbar pull-right").add {
                    div().cls("button-group").add {
                        val a = createTag("a").cls("btn btn-success btn-sm").txt("Add")

                        a.onClick {
                            val password = Password(group)
                            val passwordForm = PasswordForm(password)

                            passwordForm.password.decrypt()
                            createPasswordEditor(parent, group, passwords, passwordForm, true)
                        }

                        a
                    }
                }
            }.add {
                createTag("h4").txt("Passwords")
            }
        }

        val table = createTag("table").cls("table table-striped table-condensed table-hover")

        table.add {
            createTag("tr")
              .add { createTag("th").txt("Title") }
              .add { createTag("th").txt("Url") }
              .add { createTag("th").txt("Username") }
              .add { createTag("th").txt("") }
              .add { createTag("th").txt("") }
              .add { createTag("th").txt("") }
              .add { createTag("th").txt("") }
        }

        for (password in passwords) {
            val passwordCopy = Password(
              password.id,
              password.user,
              password.group,
              password.title,
              password.website,
              password.username,
              password.encryptedPassword,
              password.password1,
              password.password2,
              password.description)
            val passwordForm = PasswordForm(passwordCopy)

            table.add {
                createTag("tr")
                  .add { createTag("td").txt(password.title) }
                  .add { createTag("td").txt(password.website) }
                  .add { createTag("td").txt(password.username) }
                  .add {
                      createTag("td").cls("col-md-5").add {
                          IconButton.create("copy", text = "Username&nbsp;&nbsp;", buttonClass = "btn-xs btn-default") {
                              copyToClipboard(password.username)

                              Notify.show("Copied username to clipboard.", "success")
                              //ModalView.showAlert("Success!", "Copied username to clipboard!")
                          }
                      }.add {
                          IconButton.create("copy", text = "Password&nbsp;&nbsp;", buttonClass = "btn-xs btn-warning") {
                              copyToClipboard(UserState.decryptPassword(password.encryptedPassword))

                              Notify.show("Copied password to clipboard.", "success")
                              //ModalView.showAlert("Success!", "Copied password to clipboard!")
                          }.attr("style", "margin-left: 5px;")
                      }.add {
                          IconButton.create("folder-open", buttonClass = "btn-xs btn-success") {
                              passwordForm.password.decrypt()
                              createPasswordEditor(parent, group, passwords, passwordForm)
                          }.attr("style", "margin-left: 5px;")
                      }.add {
                          IconButton.create("remove", buttonClass = "btn-xs btn-danger") {
                              ModalView.showConfirm(
                                "Delete password",
                                createTag("span").txt("Are you sure you want to delete password '${password.title}'?"),
                                confirm = {
                                    password.delete()
                                    UserState.saveData()
                                    PasswordOverviewView.show(parent, group, passwords)
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
      group: Group,
      passwords: List<Password>,
      passwordForm: PasswordForm,
      newPassword: Boolean = false) {

        PasswordView.create(parent, passwordForm, cancel = {
            show(parent, group, passwords)
        }, save = {
            if (passwordForm.validate(newPassword)) {
                passwordForm.save(newPassword)

                show(parent, group, passwords)
            } else {
                createPasswordEditor(parent, group, passwords, passwordForm, newPassword)
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
        var passwordType = "password"

        if (passwordForm.showPassword) {
            passwordType = "text"
        }

        parent.add {
            if (passwordForm.password.id > 0) {
                header("Edit password")
            } else {
                header("New password")
            }
        }.add {
            Form.create(FormType.HORIZONTAL)
              .add {
                  Input.create(
                    "modal_password_title",
                    label = "Title",
                    labelWidth = 4,
                    messages = passwordForm.messages["title"],
                    value = password.title,
                    blur = { e ->
                        password.title = (e.target as HTMLInputElement).value
                    },
                    change = { e ->
                        password.title = (e.target as HTMLInputElement).value
                    }
                  )
              }.add {
                Input.create(
                  "modal_password_url",
                  label = "Url",
                  labelWidth = 4,
                  messages = passwordForm.messages["website"],
                  value = password.website,
                  blur = { e ->
                      password.website = (e.target as HTMLInputElement).value
                  },
                  change = { e ->
                      password.website = (e.target as HTMLInputElement).value
                  }
                )
            }.add {
                Input.create(
                  "modal_password_username",
                  label = "Username",
                  labelWidth = 4,
                  inputWidth = 8,
                  messages = passwordForm.messages["username"],
                  value = password.username,
                  blur = { e ->
                      password.username = (e.target as HTMLInputElement).value
                  },
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
                  blur = { e ->
                      password.password1 = (e.target as HTMLInputElement).value
                  },
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
                  blur = { e ->
                      password.password2 = (e.target as HTMLInputElement).value
                  },
                  change = { e ->
                      password.password2 = (e.target as HTMLInputElement).value
                  }
                ).add {
                    IconButton.create("cog") { e ->
                        ModalView.showConfirm("Generate password",
                          GeneratePasswordView.create(password, PasswordSettings()),
                          "Save",
                          "Cancel",
                          confirm = {
                              create(parent, passwordForm, cancel, save)
                          })
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
                  blur = { e ->
                      password.description = (e.target as HTMLTextAreaElement).value
                  },
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
                      div().cls("col-sm-offset-4 col-sm-8").add {
                          val saveButton = createTag("button").cls("btn btn-success").txt("Save")

                          saveButton.onClick { save(passwordForm) }

                          saveButton
                      }.add {
                          val cancelButton = createTag("button").cls("btn btn-default").txt("Cancel")

                          cancelButton.attr("style", "margin-left: 5px;")
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