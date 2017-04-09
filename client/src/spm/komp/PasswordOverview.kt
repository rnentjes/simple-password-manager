package spm.komp

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import nl.astraeus.komp.HtmlComponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import spm.view.modal.ModalView
import spm.view.modal.Notify
import stats.view.Modal
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by rnentjes on 4-4-17.
 */

class RemovePasswordConfirm(val password: Password) : HtmlComponent() {
    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.span {
        +"Are you sure you want to remove password '${password.title}'?"
    }
}

class RemoveGroupConfirm(val groupName: String) : HtmlComponent() {
    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.span {
        +"Are you sure you want to remove group '$groupName'?"
    }
}

class GroupNameEdit(var groupname: String = "") : HtmlComponent() {

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "") {
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


class PasswordOverview(val container: HtmlComponent) : HtmlComponent() {

    fun rename(group: Group) {
        val renameSubgroup = GroupNameEdit(group.name)
        Modal.openModal("Add group", renameSubgroup, okText = "Save", okButtonClass = "btn-success", ok = {

            if (renameSubgroup.groupname.isBlank()) {
                //Notify.show("Group name can not be blank!", "error")
                ModalView.showAlert("Error", "Group name can not be blank")
            } else {
                group.name = renameSubgroup.groupname
                UserState.saveData()
                container.refresh()
            }

            true
        })
    }

    fun addSubgroup(group: Group) {
        val addSubgroup = GroupNameEdit()
        Modal.openModal("Add group", addSubgroup, okText = "Save", okButtonClass = "btn-success", ok = {

            if (addSubgroup.groupname.isBlank()) {
                //Notify.show("Group name can not be blank!", "error")
                ModalView.showAlert("Error", "Group name can not be blank")
            } else {
                val newGroup = Group(addSubgroup.groupname, group)
                group.children.add(newGroup)

                UserState.saveData()
                container.refresh()
            }

            true
        })
    }

    fun removeGroup(group: Group) {
        val removeSubGroup = RemoveGroupConfirm(group.name)
        Modal.openModal("Remove group", removeSubGroup, okText = "Remove", okButtonClass = "btn-danger", ok = {
            group.parent?.children?.remove(group);

            UserState.saveData()
            container.refresh()

            true
        })
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-9") {
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
                div(classes = "col-md-6") {
                    style = "margin-top: 20px;"
                    a(classes = "btn btn-success btn-sm") {
                        +"Rename"
                        onClickFunction = {
                            rename(cg)
                        }
                    }
                    a(classes = "btn btn-primary btn-sm") {
                        style = "margin-left:5px;"
                        +"Add subgroup"
                        onClickFunction = {
                            addSubgroup(cg)
                        }
                    }
                    a(classes = "btn btn-danger btn-sm") {
                        style = "margin-left:5px;"
                        if (cg.children.isNotEmpty() || cg.passwords.isNotEmpty() || cg.parent == null) {
                            attributes["disabled"] = "disabled"
                        }
                        +"Remove group"
                        onClickFunction = {
                            if (cg.children.isEmpty() && cg.passwords.isEmpty() && cg.parent != null) {
                                removeGroup(cg)
                            }
                        }
                    }
                }
            }
            div(classes = "row") {
                hr {}
            }
            div {
                //id = "passwords_overview"
                div(classes = "page-header") {
                    div(classes = "btn-toolbar pull-right") {
                        div(classes = "button-group") {
                            a(classes = "btn btn-success btn-sm") {
                                +"Add"
                                onClickFunction = {
                                    val editor = PasswordEditor(cg)
                                    Modal.openModal("Edit password",
                                      editor,
                                      okText = "Save",
                                      okButtonClass = "btn-success",
                                      ok = {
                                          if (editor.validate()) {
                                              if (editor.originalPassword == null) {
                                                  editor.password.encryptedPassword = UserState.encryptPassword(editor.password.password1)
                                                  cg.passwords.add(editor.password)
                                              } else {
                                                  throw IllegalStateException("Add button modal has existing password!?")
                                              }
                                              UserState.saveData()
                                              container.refresh()

                                              true
                                          } else {

                                              false
                                          }
                                      })
                                }
                            }

                        }
                    }
                    h4 {
                        +"Passwords"
                    }
                }
                div(classes = "row") {
                    table(classes = "table table-striped table-condensed table-hover") {
                        tr {
                            th { +"Title" }
                            th { +"Url" }
                            th { +"Username" }
                            th { +"" }
                        }
                        for (password in cg.passwords) {
                            tr {
                                td { +password.title }
                                td { +password.website }
                                td { +password.username }
                                td(classes = "col-md-4") {
                                    passwordButton(consumer, "copy", text = "U&nbsp;", btnClass = "btn-xs btn-default") {
                                        spm.view.copyToClipboard(password.username)

                                        Notify.show("Copied username to clipboard.", "success")
                                    }
                                    passwordButton(consumer, "copy", text = "P&nbsp;", btnClass = "btn-xs btn-warning", buttonStyle = "margin-left: 5px;") {
                                        spm.view.copyToClipboard(UserState.decryptPassword(password.encryptedPassword))

                                        Notify.show("Copied password to clipboard.", "success")
                                    }
                                    passwordButton(consumer, "copy", text = "U&nbsp;", btnClass = "btn-xs btn-default", buttonStyle = "margin-left: 5px;") {
                                        spm.view.copyToClipboard(password.website)

                                        Notify.show("Copied password to clipboard.", "success")
                                    }
                                    passwordButton(consumer, "new-window", text = "U&nbsp;", btnClass = "btn-xs btn-default", buttonStyle = "margin-left: 5px;") {
                                        window.open(password.website, "_blank")
                                    }
                                    passwordButton(consumer, "folder-open", btnClass = "btn-xs btn-success", buttonStyle = "margin-left: 5px;") {
                                        val editor = PasswordEditor(cg, password)
                                        Modal.openModal("Edit password", editor, /*modalSize = "modal-lg", */ok = {
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
                                        })
                                    }
                                    passwordButton(consumer, "remove", btnClass = "btn-xs btn-danger", buttonStyle = "margin-left: 5px;") {
                                        Modal.openModal("Remove password",
                                          RemovePasswordConfirm(password),
                                          okButtonClass = "btn-danger",
                                          ok = {
                                              password.delete()
                                              UserState.saveData()
                                              refresh()

                                              true
                                          })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun passwordButton(
      consumer: TagConsumer<HTMLElement>,
      icon: String,
      text: String = "",
      buttonStyle: String = "",
      btnClass: String = "btn-default",
      click: (Event) -> Unit = {}) {
        consumer.button(classes = "btn $btnClass") {
            type = ButtonType.button
            if (buttonStyle.isNotBlank()) {
                style = buttonStyle
            }
            attributes["aria-label"] = text

            unsafe { +text }
            span(classes = "glyphicon glyphicon-$icon") {
                attributes["aria-hidden"] = "true"
            }

            onClickFunction = click
        }
    }

    fun copyToClipboard(text: String) {
        val ta = document.createElement("textarea")
        ta.innerHTML = text

        if (ta is HTMLTextAreaElement) {
            val body = document.body ?: throw IllegalStateException("The body was not found!")

            body.append(ta)
            ta.select()
            document.execCommand("copy")
            body.removeChild(ta)
        }
    }
}