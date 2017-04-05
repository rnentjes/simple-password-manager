package spm.komp

import kotlinx.html.*
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
import stats.view.Modal
import kotlin.js.Math

/**
 * User: rnentjes
 * Date: 5-4-17
 * Time: 9:46
 */


private val basic = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
private val numbers = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
private val special = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%*()_+-={}[]:\"|;'\\<>?,./"
private val specialNumbers = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%*()_+-={}[]:\"|;'\\<>?,./0123456789"

class PasswordGenerator(val password: Password) : HtmlComponent() {
    var passwordLength = 26
    var includeNumbers = true
    var includeSpecial = true
    var generatedPassword = UserState.decryptPassword(password.encryptedPassword)

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-12") {
        form(classes = "form form-horizontal") {
            div(classes = "form-group") {
                label(classes = "col-md-3") {
                    for_ = "password_length"
                    + "Password length"
                }
                div(classes = "col-md-9") {
                    input(classes = "form-control") {
                        id = "password_length"
                        value = passwordLength.toString()

                        fun changeLength(e: Event) {
                            passwordLength = (e.target as HTMLInputElement).value.toInt()
                        }

                        onBlurFunction = ::changeLength
                        onKeyUpFunction = ::changeLength
                    }
                }
            }
            div(classes = "form-group") {
                div(classes = "col-md-offset-3 col-md-9") {
                    div(classes = "checkbox") {
                        label {
                            for_ = "password_numbers"
                            input {
                                id = "password_numbers"
                                type = InputType.checkBox
                                checked = includeNumbers

                                onClickFunction = {
                                    includeNumbers = !includeNumbers
                                }
                            }
                            + "Numbers '0..9'"
                        }

                    }
                }
            }
            div(classes = "form-group") {
                div(classes = "col-md-offset-3 col-md-9") {
                    div(classes = "checkbox") {
                        label {
                            for_ = "password_special"
                            input {
                                id = "password_special"
                                type = InputType.checkBox
                                checked = includeSpecial

                                onClickFunction = {
                                    includeSpecial = !includeSpecial
                                }
                            }
                            + "Special '!@#$'<`~' etc"
                        }

                    }
                }
            }
            div(classes = "form-group") {
                label(classes = "col-md-3") {
                    for_ = "password_generated"
                    + "Generated pwd"
                }
                div(classes = "col-md-7") {
                    input(classes = "form-control") {
                        id = "password_generated"
                        value = generatedPassword
                    }
                }

                div(classes = "col-md-2") {
                    button(classes = "btn btn-default") {
                        type = ButtonType.button
                        attributes["aria-label"] = "Refresh"
                        span(classes = "glyphicon glyphicon-refresh") {
                            attributes["aria-hidden"] = "true"
                        }
                        onClickFunction = {
                            generatedPassword = generatePassword(passwordLength, includeNumbers, includeSpecial)

                            refresh()
                        }
                    }
                }
            }
        }
    }

    private fun generatePassword(length: Int, includeNumbers: Boolean, includeSpecial: Boolean): String {
        val builder = StringBuilder()
        val source: String
        var select = 0

        if (includeNumbers) { select += 1 }
        if (includeSpecial) { select += 2 }

        when(select) {
            0 -> { source = basic              }
            1 -> { source = numbers            }
            2 -> { source = special            }
            3 -> { source = specialNumbers     }
            else -> {
                source = specialNumbers
            }
        }

        for (index in 0..length-1) {
            builder.append(source[(source.length * Math.random()).toInt()])
        }

        return builder.toString()
    }

}

class PasswordEditor(val group: Group, val originalPassword: Password? = null) : HtmlComponent() {
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

                        + password.description
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