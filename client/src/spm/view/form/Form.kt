package spm.view.form

import org.w3c.dom.Element
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.events.Event
import spm.view.*
import spm.view.password.PasswordOverviewView
import kotlin.dom.on
import kotlin.dom.onClick

/**
 * Created by rnentjes on 20-11-16.
 */

enum class FormType(val cls: String) {
    DEFAULT(""),
    INLINE("form-inline"),
    HORIZONTAL("form-horizontal")
}

object Form {

    fun create(
      type: FormType = FormType.DEFAULT
    ): HTMLFormElement {
        val result = createTag("form") as HTMLFormElement

        result.cls(type.cls)

        return result
    }
}

object Input {

    fun create(
      id: String = "",
      name: String = id,
      type: String = "text",
      label: String = "",
      labelWidth: Int,
      inputWidth: Int = (12 - labelWidth),
      value: String = "",
      placeHolder: String = "",
      helpText: String = "",
      classes: String = "",
      change: (Event) -> Unit = {}) : Element {
        val result = div().cls("form-group")

        if (label.isNotBlank()) {
            result.with(createTag("label").attr("for", id).txt(label).cls("col-md-$labelWidth"))
        }

        val input = createTag("input")
          .attr("id", id)
          .attr("type", type)
          .attr("value", value)
          .cls("form-control $classes")

        if (placeHolder.isNotBlank()) {
            input.attr("placeholder", placeHolder)
        }

        input.on("keyup", true, change)

        result.add {
            val result = div().cls("col-md-$inputWidth")

            result.add {
                input
            }

            if (helpText.isNotBlank()) {
                result.add {
                    createTag("span").cls("help-block").txt(helpText)
                }
            }

            result
        }



        return result
    }

}

object FormButton {

    fun create(
      label: String,
      labelWidth: Int,
      buttonClass: String = "btn-default",
      click: (Event) -> Unit = {}
    ): Element {

        return div().cls("form-group").with(
          div().cls("col-sm-offset-${labelWidth} col-sm-${12-labelWidth}").with(
            createTag("button").attr("type", "submit").cls("btn ${buttonClass}").txt(label)
          )
        )
    }

}


object FormLinkButton {

    fun create(
      label: String,
      labelWidth: Int,
      buttonClass: String = "btn-default",
      click: (Event) -> Unit = {}
    ): Element {
        val a = createTag("a").cls("btn ${buttonClass}").txt(label)

        a.onClick { click(it) }

        return div().cls("form-group").with(
          div().cls("col-sm-offset-${labelWidth} col-sm-${12-labelWidth}").with(a)
        )
    }

}

object IconButton {
    fun create(
      icon: String,
      text: String = "",
      buttonClass: String = "btn-default",
      click: (Event) -> Unit = {}
    ): Element {
        val button = createTag("button")
          .attr("type", "button")
          .cls("btn btn-sm $buttonClass")
          .attr("aria-label", "Show")

        if (text.isNotBlank()) {
            button.txt(text)
        }

        button.add {
            createTag("span")
              .cls("glyphicon glyphicon-$icon")
              .attr("aria-hidden", "true")
        }

        button.onClick { e -> click(e) }

        return button
    }
}

object TextButton {
    fun create(
      text: String,
      buttonClass: String = "btn-default",
      click: (Event) -> Unit = {}
    ): Element {
        val button = createTag("button")
          .attr("type", "button")
          .cls("btn btn-sm $buttonClass")
          .attr("aria-label", "Show")
          .add {
              createTag("span").attr("aria-hidden", "true").txt(text)
          }

        button.onClick { e -> click(e) }

        return button
    }
}
