package spm.view

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.js.div
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import spm.state.UserState

/**
 * Created by rnentjes on 3-4-17.
 */

class Container(main: Komponent): Komponent() {
    val groupOverview = GroupOverview(this)
    val passwordOverview = PasswordOverview(this)
    val searchResult = SearchResult(this)

    override fun render(consumer: HtmlBuilder) = consumer.div {
        div(classes = "container") {
            include(groupOverview)
            if (UserState.currentSearch.isBlank()) {
                include(passwordOverview)
            } else {
                include(searchResult)
            }
        }
    }

}
