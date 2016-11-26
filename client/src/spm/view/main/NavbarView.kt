package spm.view.main

import org.w3c.dom.Element
import spm.http.Http
import spm.view.div

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 11:31
 */

object NavbarView {
    var html: String = "Html not loaded!"

    init {
        Http.readAsString("html/navbar.html", { data -> html = data })
    }

    fun create(parent: Element) {
        val div = div()

        div.innerHTML = html

        parent.appendChild(div)
    }

}
