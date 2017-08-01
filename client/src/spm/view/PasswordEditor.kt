package spm.view

import kotlinx.html.*
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import spm.view.input.SelectInput
import stats.view.Modal

/**
 * User: rnentjes
 * Date: 5-4-17
 * Time: 9:46
 */

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
    }

    fun validate(): Boolean {
        console.log("validating: ", password)
        return true
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-12") {
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
              change = { e ->
                  val target = e.target

                  if (target is HTMLSelectElement) {
                      val group = UserState.topGroup?.findById(target.value.toLong()) ?:
                        throw IllegalStateException("Group with id ${target.value} not found!")

                      console.log("Update group", group)
                      password.group = group
                  }
              }
            ))

            createInput(consumer, "password_title", "Title", password.title,
              blur = { e ->
                  password.title = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  password.title = (e.target as HTMLInputElement).value
              })
            createInput(consumer, "password_url", "Url", password.website,
              blur = { e ->
                  password.website = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  password.website = (e.target as HTMLInputElement).value
              })
            createInput(consumer, "password_username", "Username", password.username,
              blur = { e ->
                  password.username = (e.target as HTMLInputElement).value
              },
              change = { e ->
                  password.username = (e.target as HTMLInputElement).value
              })
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
                            onClickFunction = {
                                showPassword = !showPassword

                                refresh()
                            }
                        } else {
                            span(classes = "glyphicon glyphicon-eye-close") {
                                attributes["aria-hidden"] = "true"
                            }
                            onClickFunction = {
                                showPassword = !showPassword

                                refresh()
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
                    button(classes = "btn btn-default") {
                        type = ButtonType.button
                        attributes["aria-label"] = "Show"
                        span(classes = "glyphicon glyphicon-cog") {
                            attributes["aria-hidden"] = "true"
                        }
                        onClickFunction = {
                            val generator = PasswordGenerator(password)
                            Modal.openModal("Generate password", generator, ok = {
                                password.password1 = generator.generatedPassword
                                password.password2 = generator.generatedPassword

                                refresh()
                                true
                            })
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
    }

}


private fun createInput(
  consumer: TagConsumer<HTMLElement>,
  inputId: String,
  label: String = "",
  inputValue: String = "",
  inputType: InputType = InputType.text,
  placeholderText: String = "",
  error: String = "",
  blur: (Event) -> Unit = {},
  change: (Event) -> Unit = {}
) {
    consumer.div(classes = "form-group") {
        if (error.isNotBlank()) {
            classes += "has-error"
        }
        if (label.isNotBlank()) {
            label(classes = "col-md-3") {
                for_ = inputId
                +label
            }
        }
        div(classes = "col-md-9") {
            input(classes = "form-control") {
                id = inputId
                name = inputId
                type = InputType.text
                value = inputValue
            }
            if (placeholderText.isNotBlank()) {
                attributes["placeholder"] = placeholderText
            }
            if (error.isNotBlank()) {
                span(classes = "help-block") {
                    +error
                }
            }
        }

        onBlurFunction = blur
        onKeyUpFunction = change
    }
}
