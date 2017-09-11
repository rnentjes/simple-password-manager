package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import spm.view.button.PasswordButton
import stats.view.Modal
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by rnentjes on 4-4-17.
 */

class SearchResult(val container: Komponent) : Komponent() {

    fun findPasswords(group: Group): ArrayList<Password> {
        val result = ArrayList<Password>()

        for (password in group.passwords) {
            if (password.search(UserState.currentSearch)) {
                result.add(password)
            }
        }

        for (child in group.children) {
            result.addAll(findPasswords(child))
        }

        return result
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-9") {
        val topGroup = UserState.topGroup
        var searchResult = ArrayList<Password>()
        if (topGroup != null) {
            searchResult = findPasswords(topGroup)
        }
        div(classes = "row") {
            div(classes = "col-md-6") {
                h3 {
                    // background-color: #f8f8f8;
                    style = "text-align: center; padding: 10px; margin: 5px"

                    + "Search result for '${UserState.currentSearch}'"
                }
            }
        }
        div(classes = "row") {
            hr {}
        }
        div {
            //id = "passwords_overview"
            div(classes = "page-header") {
                h4 {
                    +"Found passwords"
                }
            }
            div(classes = "row") {
                table(classes = "table table-striped table-condensed table-hover") {
                    tr {
                        th { +"Group" }
                        th { +"Title" }
                        th { +"Url" }
                        th { +"Username" }
                        th { +"" }
                    }
                    for (password in searchResult) {
                        tr {
                            td { +password.group.name }
                            td { +password.title }
                            td { +password.website }
                            td { +password.username }
                            td(classes = "col-md-4") {
                                include(PasswordButton(
                                  "copy",
                                  text = "U ",
                                  btnClass = "btn-xs btn-default"
                                ) {
                                    copyToClipboard(password.username)

                                    Notify.show("Copied username to clipboard.", "success")
                                })
                                include(PasswordButton(
                                  "copy",
                                  text = "P ",
                                  btnClass = "btn-xs btn-warning",
                                  buttonStyle = "margin-left: 5px;"
                                ) {
                                    copyToClipboard(UserState.decryptPassword(password.encryptedPassword))

                                    Notify.show("Copied password to clipboard.", "success")
                                })
                                include(PasswordButton(
                                  "copy",
                                  text = "U ",
                                  btnClass = "btn-xs btn-default",
                                  buttonStyle = "margin-left: 5px;") {
                                    copyToClipboard(password.website)

                                    Notify.show("Copied password to clipboard.", "success")
                                })
                                include(PasswordButton(
                                  "new-window",
                                  text = "U ",
                                  btnClass = "btn-xs btn-default",
                                  buttonStyle = "margin-left: 5px;") {
                                    window.open(password.website, "_blank")
                                })
                                include(PasswordButton(
                                  "folder-open",
                                  text = "U ",
                                  btnClass = "btn-xs btn-success",
                                  buttonStyle = "margin-left: 5px;") {
                                    val editor = PasswordEditor(password.group, password)
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
                                })
                                include(PasswordButton(
                                  "remove",
                                  btnClass = "btn-xs btn-danger",
                                  buttonStyle = "margin-left: 5px;") {
                                    Modal.openModal("Remove password",
                                      RemovePasswordConfirm(password),
                                      okButtonClass = "btn-danger",
                                      ok = {
                                          password.delete()
                                          UserState.saveData()
                                          refresh()

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