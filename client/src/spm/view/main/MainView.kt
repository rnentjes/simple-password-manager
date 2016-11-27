package spm.view.main

import org.w3c.dom.Element
import spm.state.UserState
import spm.view.add
import spm.view.attr
import spm.view.cls
import spm.view.div
import spm.view.group.GroupView
import spm.view.login.LoginView
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

        container.add {
            GroupView.show(null)
        }.add {
            div().attr("id", "group_passwords_overview").cls("col-md-9")
        }

        parent.appendChild(container)
    }

    fun logout() {
        val body = document.body ?: throw IllegalStateException("document.body not defined! Are you sure this is a browser?")

        while(body.children.length > 0) {
            body.removeChild(body.firstChild!!)
        }

        // todo: move to a better place
        GroupView.currentGroup = null
        UserState.loginname = null
        UserState.loginPasswordHash = null
        UserState.decryptPassphraseHash = null
        UserState.encryptedEncryptionKey = null

        body.appendChild(LoginView.create())
    }

}
