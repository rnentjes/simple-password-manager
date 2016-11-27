package spm.view.group

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import spm.view.*
import spm.view.form.Form
import spm.view.form.FormLinkButton
import spm.view.form.FormType
import spm.view.form.Input
import spm.view.login.LoginView
import spm.view.password.Password
import spm.ws.WebSocketConnection
import java.util.*
import kotlin.browser.window
import kotlin.dom.onClick

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 12:07
 */


data class Group(
  var id: Long,
  var name: String,
  var parent: Group?,
  var opened: Boolean = false,
  val children: MutableList<Group> = ArrayList()
) {
    override fun equals(other: Any?) = super.equals(other)

    override fun hashCode() = super.hashCode()
}

object GroupView {

    /* creates <ul><li> (if children)<ul>etc</ul>(/) </li></ul? */
    fun create(group: Group): Element {
        val result: Element

        if (!hasElem("group_overview")) {
            result = div().attr("id", "group_overview").cls("col-md-3")
        } else {
            result = elem("group_overview")

            clear("group_overview")
        }

        result.add {
            div().cls("row").add {
                div().cls("col-md-6").add {
                    createTag("h1").txt("Groups ")
                }
            }. add {
                div().cls("col-md-6").add {
                    createTag("br")
                }
            }
        }.add {
            createGroup(group, group)
        }

        return result
    }

    fun createPassword(password: Password): Element {
        return createTag("ul").cls("tree").add{
            createTag("li").add {
                createTag("span").attr("style", "margin-right: 5px;").cls("glyphicon glyphicon-lock")
            }.add {
                val link = createTag("a").txt(password.title)

                link.setAttribute("href", "#")

                link.onClick {
                    clickPassword(password)
                }

                link
            }
        }
    }

    fun createGroup(topGroup: Group, group: Group): Element {
        val result = createTag("li").add {
            val icon = createTag("span").attr("style", "margin-right: 5px;")

            if (group.children.isNotEmpty()) {
                if (group.opened) {
                    icon.cls("glyphicon glyphicon-minus")
                } else {
                    icon.cls("glyphicon glyphicon-plus")
                }
            } else {
                icon.cls("glyphicon glyphicon-none")
            }

            icon.onClick {
                clickExpandGroup(topGroup, group)
            }

            icon
        }.add {
            val link = createTag("a").txt(group.name)

            link.setAttribute("href", "#")
            link.onClick {
                clickGroup(group)
            }

            link
        }

        println("Opened: ${group.id} - ${group.opened} ")
        if (group.opened) {
            //group.passwords.forEach { result.with(createPassword(it)) }
            group.children.forEach { result.with(createGroup(topGroup, it)) }
        }

        return createTag("ul").cls("tree").with(result)
    }

    fun clickGroup(group: Group) {
        GroupPasswordsView.show(group)
    }

    fun clickPassword(password: Password) {
        println("Clicked on Password: $password")
    }

    fun clickExpandGroup(topGroup: Group, group: Group) {
        println("Clicked on ExpandGroup: $group")
        group.opened = !group.opened

        create(topGroup)
    }
}

object GroupPasswordsView {

    fun show(group: Group) {
        val result: Element

        if (!hasElem("group_passwords_overview")) {
            result = div().attr("id", "group_passwords_overview").cls("col-md-3")
        } else {
            result = elem("group_passwords_overview")

            clear("group_passwords_overview")
        }

        result.add {
            div().cls("row").add {
                createTag("h1").txt(group.name)
            }.add {
                createTag("br")
            }.add {
                Form.create(FormType.HORIZONTAL).add {
                    div().cls("form-group").add {
                        div().cls("col-sm-offset-4 col-sm-8").add {
                            val a = createTag("a").cls("btn btn-primary btn-xl").txt("Create new subgroup")

                            a.onClick {
                                WebSocketConnection.loading()
                                WebSocketConnection.send("CREATEGROUP", "${group.id}")
                            }

                            a
                        }.add {
                            val a = createTag("a").cls("btn btn-danger btn-xl").txt("Remove this group")

                            if (group.children.isNotEmpty()) {
                                a.attr("disabled", "disabled")
                            }

                            a.onClick { }

                            a
                        }
                    }
                }.add {
                    Input.create("group_name", label = "Group name", labelWidth = 4, value = group.name)
                }.add {
                    FormLinkButton.create("Save", buttonClass = "btn-success btn-xl", labelWidth = 4, click = {
                        val input = elem("group_name") as HTMLInputElement

                        if (input.value.isBlank()) {
                            window.alert("Name can not be empty!")
                        } else {
                            group.name = input.value

                            WebSocketConnection.send("UPDATEGROUPNAME", "${group.id}", group.name)
                            show(group)
                        }
                    })
                }
            }
        }
    }

}
