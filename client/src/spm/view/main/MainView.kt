package spm.view.main

import org.w3c.dom.Element
import spm.view.*
import spm.view.group.Group
import spm.view.group.GroupView
import spm.view.password.Password
import java.util.*
import kotlin.browser.document

/**
 * Created by rnentjes on 22-11-16.
 */

object MainView {

    fun show() {
        val body = document.body ?: throw IllegalStateException("document.body not defined! Are you sure this is a browser?")

        while(body.children.length > 0) {
            body.removeChild(body.firstChild!!)
        }

        NavbarView.create(body)
        create(body)
    }

    fun create(parent: Element) {
        val container = div()

        container.cls("container")

        container.setAttribute("id", "id_groups")

        val group = Group(1, "Group 1", null)

        group.children.add(Group(2, "Child 1", null))
        group.children.add(Group(3, "Child 2", null))

        group.children[0].children.add(Group(4, "Child 1 - 1", null))
        group.children[0].children.add(Group(5, "Child 1 - 2", null))

        container.add {
            GroupView.create(group)
        }.add {
            div().attr("id", "group_passwords_overview").cls("col-md-9")
        }

        parent.appendChild(container)
    }

    fun createButtonBar(): Element {
/*
        <button type="button" class="btn btn-default" aria-label="Left Align">
        <span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>
        </button>
*/
        return div().cls("btn-toolbar").attr("role", "toolbar").add {
            div().cls("btn-group").add {
                createTag("button").cls("btn btn-default").attr("aria-label", "Text").add {
                    createTag("span").cls("glyphicon glyphicon-align-left").attr("aria-hidden", "true")
                }
            }.add {
                createTag("button").cls("btn btn-default").attr("aria-label", "Text").add {
                    createTag("span").cls("glyphicon glyphicon-align-left").attr("aria-hidden", "true")
                }
            }
        }
    }

}
