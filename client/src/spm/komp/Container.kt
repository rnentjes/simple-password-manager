package spm.komp

import kotlinx.html.TagConsumer
import kotlinx.html.js.div
import nl.astraeus.komp.HtmlComponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement

/**
 * Created by rnentjes on 3-4-17.
 */

class Container: HtmlComponent() {
    val navbar = Navbar()

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div {
        include(navbar)
    }

}
