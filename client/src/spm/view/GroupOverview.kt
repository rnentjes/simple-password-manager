package spm.view

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLElement
import spm.model.Group
import spm.state.UserState
import spm.state.UserState.topGroup

/**
 * Created by rnentjes on 4-4-17.
 */

class GroupOverview(val container: Komponent) : Komponent() {

    private fun createGroup(consumer: TagConsumer<HTMLElement>, topGroup: Group, group: Group) {
        consumer.ul(classes = "tree") {
            li {
                span {
                    style = "margin-right: 10px;"
                    classes += "glyphicon"

                    if (group.children.isNotEmpty()) {
                        classes += if (group.opened) {
                            "glyphicon-folder-open"
                        } else {
                            "glyphicon-folder-close"
                        }
                    } else {
                        classes += "glyphicon-folder-close"
                        style = "color: transparent;"
                    }

                    onClickFunction = {
                        group.opened = !group.opened
                        UserState.saveData()

                        refresh()
                    }
                }
                a {
                    href = "#"
                    var name = group.name

                    if (name.length > 14) {
                        name = name.slice(0..11) + "..."
                    }

                    +name

                    if (group.found) {
                        classes += "found"
                    }
                    if (group == UserState.currentGroup) {
                        classes += "selected"
                    }

                    onClickFunction = {
                        UserState.currentGroup = group
                        UserState.currentSearch = ""

                        container.refresh()
                    }

                }
                span(classes = "badge") {
                    +"${group.passwords.size}/${group.getPasswordsCountInGroup()}"
                }
                if (group.opened) {
                    group.children.forEach {
                        createGroup(consumer, topGroup, it)
                    }
                }
            }
        }
    }

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-3") {
        div(classes = "row") {
            div(classes = "col-md-12") {
                h4 {
                    + "Password groups"
                }
            }
            val tg = topGroup

            if (tg != null) {
                createGroup(consumer, tg, tg)
            }
        }
    }

}
