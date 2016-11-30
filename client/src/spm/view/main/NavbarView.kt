package spm.view.main

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.WebSocket
import spm.http.Http
import spm.state.UserState
import spm.view.div
import spm.view.elem
import spm.view.group.GroupView
import spm.ws.WebSocketConnection
import kotlin.dom.onClick

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

        elem("navbar_search").onClick {
            val searchBox = elem("navbar_search_input") as HTMLInputElement
            val root = UserState.topGroup

            if (root != null) {
                WebSocketConnection.loadingWork {
                    root.search(searchBox.value)
                    GroupView.show(root)
                }
            }
        }

        elem("logout_action").onClick {
            MainView.logout()
        }

    }

}
