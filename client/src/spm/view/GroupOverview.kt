package spm.view

import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h4
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.ul
import nl.astraeus.komp.KompConsumer
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.state.UserState
import spm.state.UserState.topGroup
import kotlin.collections.set

/**
 * Created by rnentjes on 4-4-17.
 */

class GroupOverview(val container: Komponent) : Komponent() {

  private fun openGroup(event: Event) {
    val target = event.target

    if (target is HTMLSpanElement) {
      target.getAttribute("data-group-id")?.also {
        topGroup?.findById(it.toLong())?.also {
          it.opened = !it.opened
          UserState.saveData()

          refresh()
        }
      }
    }
  }

  private fun selectGroup(event: Event) {
    val target = event.target

    if (target is HTMLAnchorElement) {
      target.getAttribute("data-group-id")?.also { id ->
        topGroup?.findById(id.toLong())?.also { group ->
          UserState.currentGroup = group
          UserState.currentSearch = ""

          container.refresh()
        }
      }
    }
  }

  private fun createGroup(consumer: KompConsumer, topGroup: Group, group: Group) {
    consumer.ul(classes = "tree") {
      li {
        span {
          style = "margin-right: 10px;"
          classes += "glyphicon"

          if (group.children.isNotEmpty()) {
            classes += if (group.opened) {
              "glyphicon-folder-open"
            } else {
              "glyphicon-folder-close"
            }
          } else {
            classes += "glyphicon-folder-close"
            style = "color: transparent;"
          }

          attributes["data-group-id"] = group.id.toString()

          onClickFunction = this@GroupOverview::openGroup
        }
        a {
          href = "#"
          var name = group.name

          if (name.length > 14) {
            name = name.slice(0..11) + "..."
          }

          +name

          if (group.found) {
            classes += "found"
          }
          if (group == UserState.currentGroup) {
            classes += "selected"
          }

          attributes["data-group-id"] = group.id.toString()

          onClickFunction = this@GroupOverview::selectGroup
        }
        span(classes = "badge") {
          +"${group.passwords.size}/${group.getPasswordsCountInGroup()}"
        }
        if (group.opened) {
          group.children.forEach {
            createGroup(consumer, topGroup, it)
          }
        }
      }
    }
  }

  override fun render(consumer: KompConsumer) = consumer.div(classes = "col-md-3") {
    div(classes = "row") {
      div(classes = "col-md-12") {
        h4 {
          +"Password groups"
        }
      }
      val tg = topGroup

      if (tg != null) {
        createGroup(consumer, tg, tg)
      }
    }
  }

}
