package spm.view.main

import org.w3c.dom.Element
import spm.view.cls
import spm.view.div
import spm.view.group.Group
import spm.view.group.GroupView
import spm.view.with
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

    /*
    <div class="row ">
    <div class="col-md-3">
        <div class="row ">
            <div class="col-md-6"><h1>Groups</h1></div>
            <div class="col-md-6">
                <br/>
                <button type="button" class="btn btn-default btn-sm" aria-label="Add">
                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                </button>
            </div>
        </div>
        <GROUPS>
      </div>
      <div class="col-md-9">
         <content>
      </div>
    </div>
     */
    fun create(parent: Element) {
        val container = div()

        container.cls("container")

        container.setAttribute("id", "id_groups")

        val group = Group(1, "Group 1", null, true)

        group.children = Array(2, { Group(0, "", null) })
        group.children[0] = Group(2, "Child 1", null, true)
        group.children[1] = Group(3, "Child 2", null, true)

        container.with(GroupView.create(group))

        parent.appendChild(container)


    }
}
