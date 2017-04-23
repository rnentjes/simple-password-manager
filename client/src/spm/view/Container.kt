package spm.view

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.js.div
import nl.astraeus.komp.HtmlComponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import spm.state.UserState

/**
 * Created by rnentjes on 3-4-17.
 */

class Container(main: HtmlComponent): HtmlComponent() {
    val navbar = Navbar(main, this)
    val groupOverview = GroupOverview(this)
    val passwordOverview = PasswordOverview(this)
    val searchResult = SearchResult(this)

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div {
        include(navbar)

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
