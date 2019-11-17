package spm.view

import kotlinx.html.*
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.model.HistoryEntry
import spm.model.Password
import spm.state.UserState
import spm.util.copyToClipboard
import spm.view.button.PasswordButton
import spm.view.input.SelectInput
import spm.view.input.TextInput
import spm.view.Modal

/**
 * User: rnentjes
 * Date: 5-4-17
 * Time: 9:46
 */

class RemoveHistoryEntryConfirm(val history: HistoryEntry) : Komponent() {
    override fun render(consumer: HtmlBuilder) = consumer.span {
        +"Are you sure you want to remove the history entry from '${history.from}' until '${history.until}?"
    }
}

class ClearHistoryConfirm : Komponent() {
    override fun render(consumer: HtmlBuilder) = consumer.span {
        +"Are you sure you want to clear the history for this password?"
    }
}

class PasswordEditor(val group: Group, val originalPassword: Password? = null) : Komponent() {
    val password: Password
    var showPassword = false

    init {
        if (originalPassword != null) {
            password = Password(originalPassword)

            password.decrypt()
        } else {
            password = Password(group)
        }

        style("nowrap", Styles.nowrap)
    }

    fun validate(): Boolean {
        return true
    }

    private fun updateGroup(e: Event) {
        val target = e.target

        if (target is HTMLSelectElement) {
            val group = UserState.topGroup?.findById(target.value.toLong()) ?:
              throw IllegalStateException("Group with id ${target.value} not found!")

            password.group = group
        }
    }

    override fun render(consumer: HtmlBuilder) = consumer.div(classes = "col-md-12") {
        val pwType = if (showPassword) {
            InputType.text
        } else {
            InputType.password
        }

        form(classes = "form form-horizontal") {

            include(SelectInput(
              "password_group",
              label = "Group",
              inputValue = "${password.group.id}",
              options = UserState.topGroup?.getGroups() ?: ArrayList(),
              readOnly = UserState.readOnly,
              change = this@PasswordEditor::updateGroup
            ))

            include(TextInput("password_title", "Title", password.title,
              readOnly = UserState.readOnly,
              blur = { e ->
                  password.title = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  password.title = (e.target as HTMLInputElement).value
              }))

            include(TextInput("password_url", "Url", password.website,
              readOnly = UserState.readOnly,
              blur = { e ->
                  password.website = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  password.website = (e.target as HTMLInputElement).value
              }))
            include(TextInput("password_username", "Username", password.username,
              readOnly = UserState.readOnly,
              blur = { e ->
                  password.username = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  password.username = (e.target as HTMLInputElement).value
              }))
            div(classes = "form-group") {
                //                if (error.isNotBlank()) {
//                    classes += "has-error"
//                }
                label(classes = "col-md-3") {
                    for_ = "password_password1"
                    +"Password"
                }
                div(classes = "col-md-7") {
                    input(classes = "form-control") {
                        id = "password_password1"
                        name = "password_password1"
                        type = pwType
                        value = password.password1
                        readonly = UserState.readOnly

                        fun changePwd1(e: Event) {
                            password.password1 = (e.target as HTMLInputElement).value
                        }

                        onBlurFunction = ::changePwd1
                        onKeyUpFunction = ::changePwd1
                    }
//                    if (error.isNotBlank()) {
//                        span(classes = "help-block") {
//                            +error
//                        }
//                    }
                }

                div(classes = "col-md-2") {
                    button(classes = "btn btn-default") {
                        type = ButtonType.button
                        attributes["aria-label"] = "Show"
                        if (showPassword) {
                            span(classes = "glyphicon glyphicon-eye-open") {
                                attributes["aria-hidden"] = "true"
                            }
                            onClickFunction = { e ->
                                e.preventDefault()

                                showPassword = !showPassword

                                update()
                            }
                        } else {
                            span(classes = "glyphicon glyphicon-eye-close") {
                                attributes["aria-hidden"] = "true"
                            }
                            onClickFunction = { e ->
                                e.preventDefault()

                                showPassword = !showPassword

                                update()
                            }
                        }
                    }
                }
            }
            div(classes = "form-group") {
                //                if (error.isNotBlank()) {
//                    classes += "has-error"
//                }
                label(classes = "col-md-3") {
                    for_ = "password_password2"
                    +"Confirm password"
                }
                div(classes = "col-md-7") {
                    input(classes = "form-control") {
                        id = "password_password2"
                        name = "password_password2"
                        type = pwType
                        value = password.password2
                        readonly = UserState.readOnly

                        fun changePwd2(e: Event) {
                            password.password2 = (e.target as HTMLInputElement).value
                        }

                        onBlurFunction = ::changePwd2
                        onKeyUpFunction = ::changePwd2
                    }
//                    if (error.isNotBlank()) {
//                        span(classes = "help-block") {
//                            +error
//                        }
//                    }
                }

                div(classes = "col-md-2") {
                    if (!UserState.readOnly) {
                        button(classes = "btn btn-default") {
                            type = ButtonType.button
                            attributes["aria-label"] = "Show"
                            span(classes = "glyphicon glyphicon-cog") {
                                attributes["aria-hidden"] = "true"
                            }
                            onClickFunction = { e ->
                                e.preventDefault()

                                val generator = PasswordGenerator(password)

                                Modal.openModal("Generate password", generator, ok = {
                                    password.password1 = generator.generatedPassword
                                    password.password2 = generator.generatedPassword

                                    update()
                                    true
                                })
                            }
                        }
                    }
                }
            }
            div(classes = "form-group") {
                //                if (error.isNotBlank()) {
//                    classes += "has-error"
//                }
                label(classes = "col-md-3") {
                    for_ = "password_notes"
                    +"Notes"
                }
                div(classes = "col-md-9") {
                    textArea(classes = "form-control") {
                        id = "password_notes"
                        rows = "4"
                        readonly = UserState.readOnly

                        +password.description
                    }
//                    if (error.isNotBlank()) {
//                        span(classes = "help-block") {
//                            +error
//                        }
//                    }
                }

                fun updateNotes(e: Event) {
                    password.description = (e.target as HTMLTextAreaElement).value
                }

                onBlurFunction = ::updateNotes
                onKeyUpFunction = ::updateNotes
            }
        }

        if (originalPassword?.hasHistory() == true) {
            div(classes = "row") {
                div(classes = "col-md-10") {
                    h5 {
                        +"Password history"
                    }
                }
                div(classes = "col-md-2") {
                    if (!UserState.readOnly) {
                        button(classes = "btn btn-danger btn-xs") {
                            type = ButtonType.button
                            attributes["aria-label"] = "Clear history"
                            title = "Clear password history"

                            +"Clear"

                            onClickFunction = {
                                Modal.openModal("Remove password",
                                                ClearHistoryConfirm(),
                                                okButtonClass = "btn-danger",
                                                ok = {
                                                    originalPassword.history.clear()
                                                    UserState.saveData()
                                                    update()

                                                    true
                                                })
                            }
                        }
                    }
                }
            }
            div(classes = "row") {
                table(classes = "table table-striped table-condensed table-hover") {
                    tr {
                        th { +"Password" }
                        th { +"From" }
                        th { +"Until" }
                        th { +"" }
                    }
                    for (history in originalPassword.history) {
                        tr {
                            td(classes = "col-md-4") {
                                + "********"
                            }
                            td(classes = "col-md-3 nowrap") {
                                + history.from
                            }
                            td(classes = "col-md-3 nowrap") {
                                + history.until
                            }
                            td(classes = "col-md-2 nowrap") {
                                include(PasswordButton(
                                  "copy",
                                  text = "P ",
                                  tooltip = "Copy password",
                                  btnClass = "btn-xs btn-warning",
                                  buttonStyle = "margin-left: 5px;"
                                ) {
                                    copyToClipboard(
                                      UserState.decryptPassword(history.encryptedPassword),
                                      this@PasswordEditor.element as HTMLElement
                                    )

                                    Notify.show("Copied password to clipboard.", "success")
                                })
                                if (!UserState.readOnly) {
                                    include(PasswordButton(
                                        "remove",
                                        tooltip = "Remove history entry",
                                        btnClass = "btn-xs btn-danger",
                                        buttonStyle = "margin-left: 5px;"
                                    ) {
                                        Modal.openModal("Remove password",
                                                        RemoveHistoryEntryConfirm(history),
                                                        okButtonClass = "btn-danger",
                                                        ok = {
                                                            originalPassword.history.remove(history)
                                                            UserState.saveData()
                                                            update()

                                                            true
                                                        })
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
