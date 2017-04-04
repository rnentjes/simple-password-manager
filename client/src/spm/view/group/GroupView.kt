package spm.view.group

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import spm.model.Group
import spm.state.UserState
import spm.view.*
import spm.view.form.Form
import spm.view.form.FormType
import spm.view.form.Input
import spm.view.modal.ModalView
import spm.view.password.PasswordOverviewView
import kotlin.dom.addClass
import kotlin.dom.removeClass

/**
 * User: rnentjes
 * Date: 26-11-16
 * Time: 12:07
 */

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
            val icon = createTag("span").attr("style", "margin-right: 10px;") as HTMLElement

            if (group.children.isNotEmpty()) {
                if (group.opened) {
                    icon.cls("glyphicon glyphicon-folder-open")
                } else {
                    icon.cls("glyphicon glyphicon-folder-close")
                }
            } else {
                icon.cls("glyphicon glyphicon-none")
            }

            icon.onclick = {
                clickExpandGroup(topGroup, group)
            }

            icon
        }.add {
            val link = createTag("a").attr("id", "group_link_${group.id}") as HTMLElement

             var name = group.name

            if (name.length > 14) {
                name = name.slice(0..11) + "..."
            }

            link.txt(name)

            if (group.found) {
                link.addClass("found")
            }

            link.setAttribute("href", "#")
            link.onclick = {
                val currentGroup = UserState.currentGroup

                if (currentGroup != null && hasElem("group_link_${currentGroup.id}")) {
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
        UserState.saveData()

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
                    val a = createTag("a").cls("btn btn-success btn-sm col-md-2").txt("Save name") as HTMLElement

                    a.onclick = {
                        val input = elem("group_name") as HTMLInputElement

                        if (input.value.isBlank()) {
                            //Notify.show("Name can not be empty!", "error")
                            ModalView.showAlert("Error", "Name can not be empty!")
                        } else {
                            group.name = input.value

                            UserState.saveData()
                            GroupView.show(UserState.topGroup)
                        }
                    }

                    a
                }.add {
                    val a = createTag("a")
                      .cls("btn btn-primary btn-sm col-md-2")
                      .attr("style", "margin-left:5px;")
                      .txt("Add subgroup") as HTMLElement

                    a.onclick = {
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
                                  val newGroup = Group(input.value, group)
                                  group.children.add(newGroup)

                                  UserState.saveData()
                                  GroupView.show(UserState.topGroup)
                              }
                          })
                    }

                    a
                }.add {
                    val a = createTag("a")
                      .cls("btn btn-danger btn-sm col-md-2")
                      .txt("Remove group")
                      .attr("style", "margin-left:5px;") as HTMLElement

                    if (group.children.isNotEmpty()) {
                        a.attr("disabled", "disabled")
                    }

                    a.onclick = { }

                    a
                }
            }
        }

        result.add {
            div().attr("id", "passwords_overview")
        }
    }
}
