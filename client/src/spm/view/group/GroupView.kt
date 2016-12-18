package spm.view.group

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import spm.state.UserState
import spm.view.*
import spm.view.form.Form
import spm.view.form.FormType
import spm.view.form.Input
import spm.view.modal.ModalView
import spm.view.password.Password
import spm.view.password.PasswordOverviewView
import spm.ws.Tokenizer
import spm.ws.WebSocketConnection
import java.util.*
import kotlin.dom.addClass
import kotlin.dom.onClick
import kotlin.dom.removeClass

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 12:07
 */


data class Group(
  var id: Long,
  var name: String,
  var opened: Boolean = false,
  var parent: Group?,
  var found: Boolean = false,
  val children: MutableList<Group> = ArrayList(),
  val passwords: MutableList<Password> = ArrayList()
) {
    constructor(tk: Tokenizer) : this(parseInt(tk.next()).toLong(), tk.next(), tk.next() == "true", null) {
        val numberOfPasswords = parseInt(tk.next())
        for (index in 0..numberOfPasswords - 1) {
            val password = Password(tk)

            passwords.add(password)
        }

        val numberOfChildren = parseInt(tk.next())

        for (index in 0..numberOfChildren - 1) {
            val child = Group(tk)

            child.parent = this
            children.add(child)
        }
    }

    override fun equals(other: Any?) = super.equals(other)

    override fun hashCode() = super.hashCode()

    fun all(): List<Group> {
        val result = ArrayList<Group>()

        result.add(this)

        for (child in children) {
            result.addAll(child.all())
        }

        return result
    }

    fun findById(id: Long): Group? {
        if (this.id == id) {
            return this
        } else {
            for (child in children) {
                val found = child.findById(id)

                if (found != null) {
                    return found
                }
            }
        }

        return null
    }

    fun search(value: String): Group? {
        var result: Group? = null

        if (name.toLowerCase().contains(value.toLowerCase())) {
            opened = false
            found = true

            result = this
            var parent = parent

            while (parent != null) {
                parent.opened = true
                parent = parent.parent
            }
        } else {
            opened = false
            found = false
        }

        if (opened) {
            var parent = parent

            while (parent != null) {
                parent.opened = true
                parent = parent.parent
            }
        }

        for (child in children) {
            val firstFound = child.search(value)

            if (result == null) {
                result = firstFound
            }
        }

        return result
    }

    fun savePassword(password: Password) {
        var found = false

        for(pwd in passwords) {
            if (pwd.id == password.id) {
                println("Replacing password ${password.id}")
                passwords.remove(pwd)
                passwords.add(password)

                found = true
                break
            }
        }

        if (!found) {
            println("Adding password ${password.id}")
            passwords.add(password)
        }
    }

    fun deletePassword(passwordId: Long) {
        for(pwd in passwords) {
            if (pwd.id == passwordId) {
                passwords.remove(pwd)
                break
            }
        }
    }

    fun getPasswordsCountInGroup(): Int {
        var result = passwords.size

        for (child in children) {
            result += child.getPasswordsCountInGroup()
        }

        return result
    }
}

object GroupView {

    /* creates <ul><li> (if children)<ul>etc</ul>(/) </li></ul? */
    fun show(group: Group?): Element {
        UserState.topGroup = group

        val result: Element

        if (!hasElem("group_overview")) {
            result = div().attr("id", "group_overview").cls("col-md-3")
        } else {
            result = elem("group_overview")

            clear("group_overview")
        }

        result.add {
            div().cls("row").add {
                div().cls("col-md-12").add {
                    createTag("h4").txt("Password groups")
                }
            }
        }

        if (group != null) {
            result.add {
                createGroup(group, group)
            }
        }

        if (UserState.currentGroup != null) {
            clickGroup(UserState.currentGroup!!)
        }

        return result
    }

    fun createGroup(topGroup: Group, group: Group): Element {
        val result = createTag("li").add {
            val icon = createTag("span").attr("style", "margin-right: 10px;")

            if (group.children.isNotEmpty()) {
                if (group.opened) {
                    icon.cls("glyphicon glyphicon-folder-open")
                } else {
                    icon.cls("glyphicon glyphicon-folder-close")
                }
            } else {
                icon.cls("glyphicon glyphicon-none")
            }

            icon.onClick {
                clickExpandGroup(topGroup, group)
            }

            icon
        }.add {
            val link = createTag("a").attr("id", "group_link_${group.id}")

             var name = group.name

            if (name.length > 14) {
                name = name.slice(0..11) + "..."
            }

            link.txt(name)

            if (group.found) {
                link.addClass("found")
            }

            link.setAttribute("href", "#")
            link.onClick {
                val currentGroup = UserState.currentGroup

                if (currentGroup != null) {
                    elem("group_link_${currentGroup.id}").removeClass("selected")
                }
                clickGroup(group)
            }

            link
        }.add {
            createTag("span").cls("badge").txt("${group.passwords.size}/${group.getPasswordsCountInGroup()}")
        }

        if (group.opened) {
            group.children.forEach { result.with(createGroup(topGroup, it)) }
        }

        return createTag("ul").cls("tree").with(result)
    }

    fun clickGroup(group: Group) {
        if (hasElem("group_link_${group.id}")) {
            elem("group_link_${group.id}").addClass("selected")
        }
        GroupPasswordsView.show(group)
        PasswordOverviewView.show(elem("passwords_overview"), group, group.passwords)
    }

    fun clickExpandGroup(topGroup: Group, group: Group) {
        group.opened = !group.opened
        WebSocketConnection.send("GROUPOPENED", "${group.id}", "${group.opened}")

        show(topGroup)
    }
}

object GroupPasswordsView {

    fun show(group: Group) {
        UserState.currentGroup = group
        val result: Element

        if (!hasElem("group_passwords_overview")) {
            result = div().attr("id", "group_passwords_overview").cls("col-md-9")
        } else {
            result = elem("group_passwords_overview")

            clear("group_passwords_overview")
        }

        result.add {
            div().cls("row").add {
                div().cls("col-md-12").add {
                    createTag("h4").txt("Group ${group.name}")
                }
            }
        }.add {
            div().cls("row").add {
                createTag("hr")
            }
        }.add {
            Form.create(FormType.HORIZONTAL).add {
                Input.create("group_name",
                  label = "Name",
                  labelWidth = 1,
                  inputWidth = 4,
                  classes = "input-lg",
                  value = group.name).add {
                    val a = createTag("a").cls("btn btn-success btn-sm col-md-2").txt("Save name")

                    a.onClick {
                        val input = elem("group_name") as HTMLInputElement

                        if (input.value.isBlank()) {
                            //Notify.show("Name can not be empty!", "error")
                            ModalView.showAlert("Error", "Name can not be empty!")
                        } else {
                            group.name = input.value

                            WebSocketConnection.send("UPDATEGROUPNAME", "${group.id}", group.name)
                            show(group)
                        }
                    }

                    a
                }.add {
                    val a = createTag("a")
                      .cls("btn btn-primary btn-sm col-md-2")
                      .attr("style", "margin-left:5px;")
                      .txt("Add subgroup")

                    a.onClick {
                        ModalView.showConfirm("New group",
                          body = Form.create(FormType.HORIZONTAL).add {
                              Input.create("modal_group_name", label = "Group name", labelWidth = 4, value = "")
                          },
                          denyText = "Close",
                          confirmText = "Save",
                          confirm = {
                              val input = elem("modal_group_name") as HTMLInputElement

                              if (input.value.isBlank()) {
                                  //Notify.show("Group name can not be blank!", "error")
                                  ModalView.showAlert("Error", "Group name can not be blank")
                              } else {
                                  WebSocketConnection.loading()
                                  WebSocketConnection.send("CREATEGROUP", "${group.id}", input.value)
                              }
                          })
                    }

                    a
                }.add {
                    val a = createTag("a")
                      .cls("btn btn-danger btn-sm col-md-2")
                      .txt("Remove group")
                      .attr("style", "margin-left:5px;")

                    if (group.children.isNotEmpty()) {
                        a.attr("disabled", "disabled")
                    }

                    a.onClick { }

                    a
                }
            }
        }

        result.add {
            div().attr("id", "passwords_overview")
        }
    }

}
