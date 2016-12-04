package spm.view.group

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import spm.state.UserState
import spm.view.*
import spm.view.form.Form
import spm.view.form.FormType
import spm.view.form.Input
import spm.view.form.InputDefinition
import spm.view.modal.ModalView
import spm.ws.Tokenizer
import spm.ws.WebSocketConnection
import java.util.*
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
  var found: Boolean = false,
  val children: MutableList<Group> = ArrayList()
) {
    constructor(tk: Tokenizer) : this(parseInt(tk.next()).toLong(), tk.next(), null, false) {
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

    fun search(value: String) {
        if (name.toLowerCase().contains(value.toLowerCase())) {
            opened = false
            found = true

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
            child.search(value)
        }
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
                div().cls("col-md-6").add {
                    createTag("h1").txt("Groups ")
                }
            }.add {
                div().cls("col-md-6").add {
                    createTag("br")
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
            val link = createTag("a")

            if (group.found) {
                link.add {
                    createTag("strong").txt(group.name)
                }
            } else {
                link.txt(group.name)
            }

            link.setAttribute("href", "#")
            link.onClick {
                clickGroup(group)
            }

            link
        }

        if (group.opened) {
            //group.passwords.forEach { result.with(createPassword(it)) }
            group.children.forEach { result.with(createGroup(topGroup, it)) }
        }

        return createTag("ul").cls("tree").with(result)
    }

    fun clickGroup(group: Group) {
        GroupPasswordsView.show(group)

        WebSocketConnection.send("GETPASSWORDS", "${group.id}")
    }

    fun clickExpandGroup(topGroup: Group, group: Group) {
        group.opened = !group.opened

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
            Form.create(FormType.HORIZONTAL).add {
                Input.create("group_name",
                  label = "",
                  labelWidth = 1,
                  inputWidth = 7,
                  classes = "input-lg",
                  value = group.name).add {
                    val a = createTag("a").cls("btn btn-success btn-sm col-md-2").txt("Save name")

                    a.onClick {
                        val input = elem("group_name") as HTMLInputElement

                        if (input.value.isBlank()) {
                            ModalView.showAlert("Error", "Name can not be empty!")
                        } else {
                            group.name = input.value

                            WebSocketConnection.send("UPDATEGROUPNAME", "${group.id}", group.name)
                            show(group)
                        }
                    }

                    a
                }
    //            createTag("h1").txt(group.name)
            }.add {
                div().cls("form-group").add {
                    div().cls("col-sm-offset-4 col-sm-8").add {
                        val a = createTag("a").cls("btn btn-primary").txt("Add subgroup")

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
                                      ModalView.showAlert("Error", "Group name can not be blank!", "Ok")
                                  } else {
                                      WebSocketConnection.loading()
                                      WebSocketConnection.send("CREATEGROUP", "${group.id}", input.value)
                                  }
                              })
                        }

                        a
                    }.add {
                        val a = createTag("a")
                          .cls("btn btn-danger btn-xl")
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
/*
              .add {
                Input.create("group_name", label = "Group name", labelWidth = 4, inputWidth = 7, value = group.name).add {
                    val a = createTag("a").cls("btn btn-success btn-xl col-md-1").txt("Save")

                    a.onClick {
                        val input = elem("group_name") as HTMLInputElement

                        if (input.value.isBlank()) {
                            ModalView.showAlert("Error", "Name can not be empty!")
                        } else {
                            group.name = input.value

                            WebSocketConnection.send("UPDATEGROUPNAME", "${group.id}", group.name)
                            show(group)
                        }
                    }

                    a
                }
            }
*/
        }

        result.add {
            div().attr("id", "passwords_overview")
        }
    }

}
