package spm.view

import kotlinx.html.*
import kotlinx.html.js.nav
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import kotlinx.html.js.onSubmitFunction
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import spm.state.UserState
import spm.view.Modal

/**
 * Created by rnentjes on 3-4-17.
 */
class Navbar(val main: Komponent, val container: Komponent): Komponent() {
    var search = UserState.currentSearch

    fun searchPasswords(e: Event) {
        e.preventDefault()

        UserState.currentSearch = search
        UserState.currentGroup = null

        container.refresh()
    }

    fun logout(e: Event) {
        UserState.logout()

        main.refresh()
    }

    fun settings(e: Event) {
        val changePassword = ChangePassword()

        Modal.openModal(
          "Change passphrase",
          changePassword,
          okText = "Update passphrase",
          showCancel = true,
          ok = {
              UserState.updatePassword(
                changePassword.currentPassword,
                changePassword.newPassword1,
                changePassword.newPassword2
              )

              true
          })
    }

    override fun render(consumer: HtmlBuilder) = consumer.nav(classes="navbar navbar-default navbar-static-top") {
        div(classes = "container-fluid") {
            if (UserState.readOnly) {
                style = "background-color: #fffdab"
            }
            div(classes = "navbar-header") {
                button(classes = "navbar-toggle collapsed") {
                    attributes.put("data-toggle", "collapse")
                    attributes.put("data-target", "#bs-example-navbar-collapse-1")
                    attributes.put("aria-expanded", "false")

                    span(classes = "sr-only") { + "Toggle navigation" }
                    span(classes = "icon-bar")
                    span(classes = "icon-bar")
                    span(classes = "icon-bar")
                }
                a(classes = "navbar-brand") {
                    href = "#"
                    if (UserState.readOnly) {
                        +"Simple password manager (read only)"
                    } else {
                        +"Simple password manager"
                    }
                }
            }

            div(classes = "collapse navbar-collapse") {
                id = "bs-example-navbar-collapse-1"
                ul(classes = "nav navbar-nav navbar-right") {
                    li {
                        a {
                            href = "#"
                            style = "font-size: large;"
                            span(classes = "glyphicon glyphicon-cog")

                            onClickFunction = this@Navbar::settings
                        }
                    }

                    li {
                        a {
                            href = "#"
                            style = "font-size: large;"
                            span(classes = "glyphicon glyphicon-off")

                            onClickFunction = this@Navbar::logout
                        }
                    }
                }
                form(classes = "navbar-form navbar-right") {


                    onSubmitFunction = this@Navbar::searchPasswords

                    div(classes = "form-group") {
                        input(classes = "form-control") {
                            type = InputType.text
                            placeholder = "Search"
                            value = search

                            onKeyUpFunction = { e ->
                                search = (e.target as HTMLInputElement).value
                            }
                        }
                    }
                    button(classes = "btn btn-default") {
                        type = ButtonType.button
                        + "Search"


                        onClickFunction = this@Navbar::searchPasswords
                    }
                }
            }
        }
    }


}
