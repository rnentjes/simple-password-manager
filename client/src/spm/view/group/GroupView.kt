package spm.view.group

import org.w3c.dom.Element
import spm.view.*
import spm.view.password.Password
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
  var children: Array<Group> = Array(0, { Group(0, "", null) }),
  var passwords: Array<Password> = Array(0, { Password(0, "", null) })
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
                }.add {
                    createTag("button").cls("btn btn-default btn-sm").attr("aria-label", "Add").add {
                        createTag("span").cls("glyphicon glyphicon-plus").attr("aria-hidden", "true")
                    }
                }
            }
        }.add {
            createGroup(group, group)
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
            val link = createTag("a").txt(group.name)

            link.setAttribute("href", "#")
            link.onClick {
                clickGroup(group)
            }

            link
        }

        println("Opened: ${group.id} - ${group.opened} ")
        if (group.opened) {
            group.children.forEach { result.with(createGroup(topGroup, it)) }
        }

        return createTag("ul").cls("tree").with(result)
    }

    fun clickGroup(group: Group) {
        println("Clicked on Group: $group")
    }

    fun clickExpandGroup(topGroup: Group, group: Group) {
        println("Clicked on ExpandGroup: $group")
        group.opened = !group.opened

        create(topGroup)
    }
}