package spm.view.group

import org.w3c.dom.Element
import spm.view.createTag
import spm.view.txt
import spm.view.with
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
  var visible: Boolean = false,
  var children: Array<Group> = Array(0, { Group(0, "", null) })
)

object GroupView {
    /*
        <ul>
            <li><a href="#"><strong>TECH</strong></a>

                <ul>
                    <li>Company Maintenance</li>
                    <li>Employees
                        <ul>
                            <li>Reports
                                <ul>
                                    <li><a href="#" id="link_1">Link 1</a></li>
                                    <li>Report2</li>
                                    <li>Report3</li>
                                </ul>
                            </li>
                            <li>Employee Maint.</li>
                        </ul>
                    </li>
                    <li>Human Resources</li>
                </ul>
            </li>
        </ul>
     */

    /* creates <ul><li> (if children)<ul>etc</ul>(/) </li></ul? */
    fun create(group: Group): Element {
        val result = createTag("li")

        val link = createTag("a").txt(group.name)

        link.setAttribute("href", "#")
        link.onClick {
            clickGroup(group)
        }

        result.with(link)

        group.children
          .filter { it.visible }
          .forEach { result.with(create(it)) }


        return createTag("ul").with(result)
    }

    fun update(element: Element, group: Group) {
        // clear children
        // append element with create(group)
    }

    fun clickGroup(group: Group) {
        println("Clicked on Group: $group")
    }
}