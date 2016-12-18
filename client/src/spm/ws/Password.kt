package spm.ws

import org.w3c.dom.WebSocket
import spm.state.UserState
import spm.view.elem
import spm.view.group.GroupView
import spm.view.password.Password
import spm.view.password.PasswordOverviewView

/**
 * Created by rnentjes on 16-12-16.
 */

fun savedPassword(ws: WebSocket, tk: Tokenizer) {
    val groupId = parseInt(tk.next()).toLong()
    val password = Password(tk)

    val topGroup = UserState.topGroup ?: throw IllegalStateException("TopGroup not found!")
    val group = topGroup.findById(groupId) ?: throw IllegalStateException("Group not found!")

    group.savePassword(password)

    GroupView.show(topGroup)
    PasswordOverviewView.show(elem("passwords_overview"), group, group.passwords)
}

fun deletedPassword(ws: WebSocket, tk: Tokenizer) {
    val groupId = parseInt(tk.next()).toLong()
    val passwordId = parseInt(tk.next()).toLong()

    val topGroup = UserState.topGroup ?: throw IllegalStateException("TopGroup not found!")
    val group = topGroup.findById(groupId) ?: throw IllegalStateException("Group not found!")

    group.deletePassword(passwordId)

    PasswordOverviewView.show(elem("passwords_overview"), group, group.passwords)
}