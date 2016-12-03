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
    constructor(): this(0, "", 0, "", "", "", "", "", "", "")

    constructor(group: Group): this(0, "", group.id, "", "", "", "", "", "", "")

    constructor(tk: Tokenizer): this(
      parseInt(tk.next()).toLong(),
      tk.next(),
      parseInt(tk.next()).toLong(),
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next(),
      tk.next(),
      "",
      "")

    fun tokenized(): String = Tokenizer.tokenize("$id", user, "$group", title, website, username, encryptedPassword, description)

    fun validate(): Boolean {
        return password1 == password2
    }

    fun descrypt() {
        password1 = UserState.decryptPassword(encryptedPassword)
        password2 = password1
    }

    fun save() {
        if (validate()) {
            encryptedPassword = UserState.encryptPassword(password1)
            if (id == 0L) {
                WebSocketConnection.send("NEWPASSWORD", "$group", title, website, username, encryptedPassword, description)
            } else {
                WebSocketConnection.send("SAVEPASSWORD", "$group", "$id", title, website, username, encryptedPassword, description)
            }
            password1 = ""
            password2 = ""
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
                        val form = Password()

                        form.group = group

                        fun save(pw: Password) {
                            if (form.validate()) {
                                form.save()

                                show(parent, group, passwords)
                            } else {
                                PasswordView.create(parent, form, cancel = {
                                    show(parent, group, passwords)
                                }, save = { save(it) })
                            }
                        }

                        PasswordView.create(parent, form, cancel = {
                            show(parent, group, passwords)
                        }, save = { save(it) })
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
                              PasswordView.create(parent, password, cancel = {
                                  show(parent, group, passwords)
                              }, save = {
                                  if (password.validate()) {
                                      password.save()

                                      show(parent, group, passwords)
                                  }
                              })
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
}

object PasswordView {

    fun create(
      parent: Element,
      passwordForm: Password,
      cancel: (Password) -> Unit = {},
      save: (Password) -> Unit = {}
    ) {
        clear(parent)

        parent.add {
            Form.create(FormType.HORIZONTAL)
              .add {
                  Input.create(
                    "modal_password_title",
                    label = "Title",
                    labelWidth = 4,
                    value = passwordForm.title) { e ->
                      passwordForm.username = (e.target as HTMLInputElement).value
                  }
              }.add {
                Input.create(
                  "modal_password_url",
                  label = "Url",
                  labelWidth = 4,
                  value = passwordForm.website) { e ->
                    passwordForm.website = (e.target as HTMLInputElement).value
                }
            }.add {
                Input.create(
                  "modal_password_username",
                  label = "Username",
                  labelWidth = 4,
                  inputWidth = 8,
                  value = passwordForm.username,
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
                  value = passwordForm.password1,
                  change = { e ->
                      passwordForm.password1 = (e.target as HTMLInputElement).value
                      //checkForm()
                  }
                ).attr("id", "modal_password_password1_warning")
                  .attr("data-show-password", "false")
                  .add {
                      IconButton.create("eye-open") { e ->
                          //switchPasswordView(e.target)
                      }
                  }
            }.add {
                Input.create(
                  "modal_password_password2",
                  type = "password",
                  label = "Confirm password",
                  labelWidth = 4,
                  inputWidth = 7,
                  value = passwordForm.password2,
                  change = { e ->
                      passwordForm.password2 = (e.target as HTMLInputElement).value
                      //checkForm()
                  }
                ).add {
                    IconButton.create("cog") { e ->
                        //switchPasswordView(e.target)
                    }
                }
            }.add {
                TextArea.create(
                  "modal_password_notes",
                  label = "Notes",
                  labelWidth = 4,
                  inputWidth = 8,
                  value = passwordForm.description,
                  change = { e ->
                      passwordForm.description = (e.target as HTMLTextAreaElement).value
                      //checkForm()
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
                      div().cls("col-sm-2").add{
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