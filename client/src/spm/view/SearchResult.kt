package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
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
                        this@table.include(PasswordOverviewRow(password))
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

            body.appendChild(ta)
            ta.select()
            document.execCommand("copy")
            body.removeChild(ta)
        }
    }
}