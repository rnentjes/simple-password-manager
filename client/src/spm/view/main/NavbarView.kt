package spm.view.main

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import spm.http.Http
import spm.state.UserState
import spm.view.div
import spm.view.elem
import spm.view.group.GroupView
import spm.view.modal.Notify
import spm.ws.WebSocketConnection

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 11:31
 */

object NavbarView {
    var html: String = ""

    fun create(parent: Element) {
        val div = div()

        parent.appendChild(div)

        loadContent(div)
    }

    private fun loadContent(parent: Element) {
        if (html.isBlank()) {
            Http.readAsString("html/navbar.html", { data ->
                html = data

                loadContent(parent)
            })
        } else {
            parent.innerHTML = html
            val search = elem("navbar_search") as HTMLElement

            search.onclick = { event ->
                val searchBox = elem("navbar_search_input") as HTMLInputElement
                val root = UserState.topGroup

                if (root != null) {
                    WebSocketConnection.loadingWork {
                        val first = root.search(searchBox.value)

                        if (first != null) {
                            UserState.currentGroup = first
                        } else {
                            Notify.show("Nothing found.", "info")
                        }

                        GroupView.show(root)
                    }
                } else {
                    println("Topgroup not found!?")
                }

                event.preventDefault()
            }

            val logout = elem("logout_action") as HTMLElement

            logout.onclick = {
                MainView.logout()
            }
        }
    }



}
