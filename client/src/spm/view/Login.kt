package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onInputFunction
import kotlinx.html.js.onKeyDownFunction
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import spm.state.UserState
import spm.ws.WebSocketConnection
import stats.view.Modal

/**
 * Created by rnentjes on 3-4-17.
 */

enum class LoginTab {
    LOGIN,
    REGISTER
}

class LoginForm(
  var login: String = "",
  var pwd1: String = "",
  var pwd2: String = ""
)

class Login : Komponent() {
    var activeTab = LoginTab.LOGIN
    var loginForm = LoginForm()

    fun login() {
        if (loginForm.login.isBlank()) {
            Modal.showAlert("Error", "Login name must be filled in!")
        } else if (loginForm.pwd1.isBlank()) {
            Modal.showAlert("Error", "Password must be filled in!")
        } else {
            UserState.loginname = loginForm.login

            WebSocketConnection.loadingWork {
                UserState.setPassword(loginForm.pwd1)

                WebSocketConnection.send("LOGIN",
                  UserState.loginname ?: throw IllegalStateException("Whut!"),
                  UserState.loginPasswordHash ?: throw IllegalStateException("Whut!")
                )
            }
        }
    }

    fun register() {
        if (loginForm.login.isBlank()) {
            Modal.showAlert("Error", "Login name must be filled in!")
        } else if (loginForm.pwd1.isBlank()) {
            Modal.showAlert("Error", "Password must be filled in!")
        } else if (loginForm.pwd1 != loginForm.pwd2) {
            Modal.showAlert("Error", "Passwords must match!")
        } else {
            UserState.loginname = loginForm.login

            WebSocketConnection.loadingWork {
                UserState.setPassword(loginForm.pwd1)

                WebSocketConnection.send("REGISTER",
                  UserState.loginname ?: throw IllegalStateException("Whut!"),
                  UserState.loginPasswordHash ?: throw IllegalStateException("Whut!"),
                  UserState.createEncryptionKey()
                )
            }
        }
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "container") {
        div(classes = "row") {
            div(classes = "col-md-6 col-md-offset-3") {
                h2 {
                    style = "text-align: center; margin-top: 40px;"
                    +"Simple password manager"
                }
            }
            div(classes = "col-md-6 col-md-offset-3") {
                style = "margin-top: 50px;"
                ul(classes = "nav nav-tabs nav-justified") {
                    li {
                        if (activeTab == LoginTab.LOGIN) {
                            classes += "active"
                        }
                        role = "presentation"
                        a {
                            +"Login"
                            onClickFunction = {
                                activeTab = LoginTab.LOGIN

                                refresh()
                            }
                        }
                    }
                    li {
                        if (activeTab == LoginTab.REGISTER) {
                            classes += "active"
                        }
                        role = "presentation"
                        a {
                            +"Register"
                            onClickFunction = {
                                activeTab = LoginTab.REGISTER

                                refresh()
                            }
                        }
                    }
                }

                div(classes = "row") { unsafe { +"&nbsp;" } }

                if (activeTab == LoginTab.LOGIN) {
                    div(classes = "form-horizontal") {
                        div(classes = "form-group") {
                            label(classes = "col-md-4") {
                                for_ = "login_name"
                                +"Login name"
                            }
                            div(classes = "col-md-8") {
                                input(classes = "form-control") {
                                    id = "login_name"
                                    type = InputType.text
                                    onInputFunction = { e ->
                                        val target = e.target
                                        if (target is HTMLInputElement) {
                                            loginForm.login = target.value
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "form-group") {
                            label(classes = "col-md-4") {
                                for_ = "login_password"
                                +"Passphrase"
                            }
                            div(classes = "col-md-8") {
                                input(classes = "form-control") {
                                    id = "login_password"
                                    type = InputType.password
                                    onInputFunction = { e ->
                                        val target = e.target
                                        if (target is HTMLInputElement) {
                                            loginForm.pwd1 = target.value
                                        }
                                    }
                                    onKeyDownFunction = { e ->
                                        if (e is KeyboardEvent) {
                                            if (e.keyCode == 13) {
                                                login()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "form-group") {
                            div(classes = "col-sm-offset-4 col-sm-8") {
                                a(classes = "btn btn-success") {
                                    +"Login"
                                    onClickFunction = {
                                        login()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    div(classes = "form-horizontal") {
                        div(classes = "form-group") {
                            label(classes = "col-md-4") {
                                for_ = "register_name"
                                +"Login name"
                            }
                            div(classes = "col-md-8") {
                                input(classes = "form-control") {
                                    id = "register_name"
                                    type = InputType.text
                                    onInputFunction = { e ->
                                        val target = e.target
                                        if (target is HTMLInputElement) {
                                            loginForm.login = target.value
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "form-group") {
                            label(classes = "col-md-4") {
                                for_ = "register_password"
                                +"Passphrase"
                            }
                            div(classes = "col-md-8") {
                                input(classes = "form-control") {
                                    id = "register_password"
                                    type = InputType.password
                                    onInputFunction = { e ->
                                        val target = e.target
                                        if (target is HTMLInputElement) {
                                            loginForm.pwd1 = target.value
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "form-group") {
                            label(classes = "col-md-4") {
                                for_ = "register_password2"
                                +"Confirm passphrase"
                            }
                            div(classes = "col-md-8") {
                                input(classes = "form-control") {
                                    id = "register_password2"
                                    type = InputType.password
                                    onInputFunction = { e ->
                                        val target = e.target
                                        if (target is HTMLInputElement) {
                                            loginForm.pwd2 = target.value
                                        }
                                    }
                                    onKeyDownFunction = { e ->
                                        if (e is KeyboardEvent) {
                                            if (e.keyCode == 13) {
                                                register()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        div(classes = "col-md-offset-4 col-md-8") {
                            span(classes = "help-block") {
                                style = "color: red;"
                                +"Please note that if you lose your passphrase there is no way to restore it. We don't know and we don't store your passphrase, so make sure you don't forget it!"
                            }
                        }
                        div(classes = "form-group") {
                            div(classes = "col-sm-offset-4 col-sm-8") {
                                a(classes = "btn btn-warning") {
                                    +"Register"
                                    onClickFunction = {
                                        register()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}