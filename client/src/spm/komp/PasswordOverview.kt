package spm.komp

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.HtmlComponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Password
import spm.state.UserState
import spm.view.modal.Notify
import stats.view.Modal
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by rnentjes on 4-4-17.
 */

class RemovePasswordConfirm(val password: Password) : HtmlComponent() {
    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.span {
        +"Are you sure you want to delete password '${password.title}'?"
    }
}

class PasswordOverview(val container: HtmlComponent) : HtmlComponent() {

    fun rename() {}

    fun addSubgroup() {}

    fun removeGroup() {}

    // language=html
    val html = """
        <a class="btn btn-success btn-sm col-md-2">Save name</a>
        <a class="btn btn-primary btn-sm col-md-2" style="margin-left:5px;">Add subgroup</a>
        <a class="btn btn-danger btn-sm col-md-2" style="margin-left:5px;" disabled="disabled">Remove group</a></div>
        """

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-9") {
        val cg = UserState.currentGroup
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
                            rename()
                        }
                    }
                    a(classes = "btn btn-primary btn-sm") {
                        style = "margin-left:5px;"
                        +"Add subgroup"
                        onClickFunction = {
                            addSubgroup()
                        }
                    }
                    a(classes = "btn btn-danger btn-sm") {
                        style = "margin-left:5px;"
                        attributes["disabled"] = "disabled"
                        +"Remove group"
                        onClickFunction = {
                            removeGroup()
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
                    val cg = UserState.currentGroup
                    if (cg != null) {
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
                                                        console.log("blank pwd: ", password)
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

            unsafe { + text }
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