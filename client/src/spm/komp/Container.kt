package spm.komp

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.js.div
import nl.astraeus.komp.HtmlComponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement

/**
 * Created by rnentjes on 3-4-17.
 */

class Container: HtmlComponent() {
    val navbar = Navbar()
    val groupOverview = GroupOverview(this)
    val passwordOverview = PasswordOverview(this)

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div {
        include(navbar)

        div(classes = "container") {
            include(groupOverview)
            include(passwordOverview)
        }
    }

}
