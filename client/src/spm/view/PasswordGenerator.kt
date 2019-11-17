package spm.view

import kotlinx.html.*
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import spm.model.Password
import spm.state.UserState
import kotlin.js.Math

/**
 * User: rnentjes
 * Date: 5-4-17
 * Time: 9:46
 */

private val basic           = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
private val numbers         = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
private val special         = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%*()_+-={}[]:\"|;'\\<>?,./"
private val specialNumbers  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%*()_+-={}[]:\"|;'\\<>?,./0123456789"

class PasswordGenerator(val password: Password) : Komponent() {
    var passwordLength = 26
    var includeNumbers = true
    var includeSpecial = true
    var generatedPassword = UserState.decryptPassword(password.encryptedPassword)

    override fun render(consumer: HtmlBuilder) = consumer.div(classes = "col-md-12") {
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
                        onClickFunction = { e ->
                            e.preventDefault()

                            generatedPassword = generatePassword(passwordLength, includeNumbers, includeSpecial)

                            update()
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
