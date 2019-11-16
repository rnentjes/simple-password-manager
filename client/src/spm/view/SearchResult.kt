package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import kotlin.browser.document

/**
 * Created by rnentjes on 4-4-17.
 */

class SearchResult(val container: Komponent) : Komponent() {

    fun findPasswords(group: Group): ArrayList<Password> {
        val result = ArrayList<Password>()
        val searchValue = UserState.currentSearch.trim().toLowerCase()

        for (password in group.passwords) {
            if (password.search(searchValue)) {
                result.add(password)
            }
        }

        for (child in group.children) {
            result.addAll(findPasswords(child))
        }

        return result
    }

    override fun render(consumer: HtmlBuilder) = consumer.div(classes = "col-md-9") {
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
        hr {}
        div {
            //id = "passwords_overview"
            div(classes = "page-header") {
                h4 {
                    +"Found passwords"
                }
            }
            div(classes = "row") {
                div(classes = "col-md-12") {
                  passwordTable(searchResult, container, true)
                }
            }
        }
    }
}
