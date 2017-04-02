package spm.view.login

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import spm.state.UserState
import spm.view.*
import spm.view.form.Form
import spm.view.form.FormLinkButton
import spm.view.form.FormType
import spm.view.form.Input
import spm.view.modal.ModalView
import spm.ws.WebSocketConnection
import kotlin.dom.addClass
import kotlin.dom.removeClass

/**
 * Created by rnentjes on 20-11-16.
 */

object LoginView {
    var showLogin: Boolean = true

    fun create(): Element {
        showLogin = true
        val result = div()

        result.add {
            div().cls("row").add {
                div().cls("col-md-6 col-md-offset-3").add {
                    createTag("h1").txt("Simple password manager").attr("style", "text-align: center;")
                }
            }
        }

        result.attr("style", "margin-top: 50px;")
        result.setAttribute("class", "container")
        result.setAttribute("id", "main")

        val div = div().cls("col-md-6 col-md-offset-3").attr("style", "margin-top: 50px;")

        div.with(createTag("ul").cls("nav nav-tabs nav-justified").with(createLoginTab()).with(createRegisterTab()))

        div.with(createLogin())
        div.with(createRegister())

        result.with(div().cls("row").with(div))

        return result
    }

    private fun createLoginTab(): Element {
        val result = createTag("li").attr("id", "login_tab").attr("role", "presentation").cls("active")

        if (showLogin) {
            result.cls("active")
        }

        val link = createTag("a").txt("Login") as HTMLElement

        result.with(link)

        link.onclick = {
            if (!showLogin) {
                showLogin = true

                elem("register_tab").removeClass("active")
                elem("login_tab").addClass("active")

                elem("login_form").attr("style", "display: block;")
                elem("register_form").attr("style", "display: none;")
            }
        }

        return result
    }

    private fun createRegisterTab(): Element {
        val result = createTag("li").attr("id", "register_tab").attr("role", "presentation")

        if (!showLogin) {
            result.cls("active")
        }

        val link = createTag("a").txt("Register") as HTMLElement

        result.with(link)

        link.onclick = {
            if (showLogin) {
                showLogin = false

                elem("login_tab").removeClass("active")
                elem("register_tab").addClass("active")

                elem("login_form").attr("style", "display: none;")
                elem("register_form").attr("style", "display: block;")
            }
        }

        return result
    }

    private fun createLogin(): Element {
        val result = div().attr("id", "login_form")

        if (!showLogin) {
            result.attr("style", "display: none;")
        }

        result.add { div().cls("row").txt("&nbsp;") }
        result.add {
            Form.create(FormType.HORIZONTAL).add {
                Input.create("login_name", label = "Login name", labelWidth = 4)
            }.add {
                val pwInput = Input.create("login_password", type = "password", label = "Passphrase", labelWidth = 4) as HTMLElement

                pwInput.onkeypress = { e ->
                    if (e is KeyboardEvent && e.keyCode === 13) {
                        login()
                    }
                }

                pwInput
            }.add {
                FormLinkButton.create("Login", buttonClass = "btn-success", labelWidth = 4, click = {
                    login()
                })
            }
        }

        return result
    }

    private fun createRegister(): Element {
        val result = div().attr("id", "register_form")

        if (showLogin) {
            result.attr("style", "display: none;")
        }

        result.add {
            div().cls("row").txt("&nbsp;")
        }.add {
            Form.create(FormType.HORIZONTAL).add {
                Input.create("register_name", label = "Login name", labelWidth = 4)
            }.add {
                Input.create("register_password", type = "password", label = "Passphrase", labelWidth = 4)
            }.add {
                val pwInput = Input.create("register_password2", type = "password", label = "Confirm passphrase", labelWidth = 4,
                  helpText = "Please note that if you lose your password there is no way to restore it.\n" +
                    "We don't know and we don't store your password, so make sure you don't forget it!") as HTMLElement

                pwInput.onkeypress = { e ->
                    if (e is KeyboardEvent && e.keyCode === 13) {
                        register()
                    }
                }

                pwInput
            }.add {
                FormLinkButton.create("Register", buttonClass = "btn-warning btn-xl", labelWidth = 4, click = {
                    register()
                })
            }
        }

        return result
    }

    fun login() {
        val username = elem("login_name") as HTMLInputElement
        val password = elem("login_password") as HTMLInputElement

        if (username.value.isBlank()) {
            ModalView.showAlert("Error", "Login name must be filled in!")
        } else if (password.value.isBlank()) {
            ModalView.showAlert("Error", "Password must be filled in!")
        } else {
            UserState.loginname = username.value

            WebSocketConnection.loadingWork {
                UserState.setPassword(password.value)

                WebSocketConnection.send("LOGIN",
                  UserState.loginname ?: throw IllegalStateException("Whut!"),
                  UserState.loginPasswordHash ?: throw IllegalStateException("Whut!")
                )
            }
        }
    }

    fun register() {
        val username = elem("register_name") as HTMLInputElement
        val password = elem("register_password") as HTMLInputElement
        val password2 = elem("register_password2") as HTMLInputElement

        if (username.value.isBlank()) {
            ModalView.showAlert("Error", "Login name must be filled in!")
        } else if (password.value.isBlank()) {
            ModalView.showAlert("Error", "Password must be filled in!")
        } else if (password.value != password2.value) {
            ModalView.showAlert("Error", "Passwords must match!")
        } else {
            UserState.loginname = username.value

            WebSocketConnection.loadingWork {
                UserState.setPassword(password.value)

                WebSocketConnection.send("REGISTER",
                  UserState.loginname ?: throw IllegalStateException("Whut!"),
                  UserState.loginPasswordHash ?: throw IllegalStateException("Whut!"),
                  UserState.createEncryptionKey()
                )
            }
        }
    }
}
