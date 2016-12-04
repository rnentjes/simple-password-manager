package spm.view.form

import org.w3c.dom.Element
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import spm.view.*
import spm.view.group.Group
import spm.ws.Tokenizer
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

enum class InputType {
    TEXT,
    PASSWORD,
    TEXTAREA
}

class InputDefinition(
  val id: String,
  val label: String,
  val name: String = id,
  val type: InputType = InputType.TEXT,
  val value: (Any) -> String = { "" },
  val save: (Any, Element) -> Unit = { a,b -> }
)

fun formTest() {

    val group = Group(Tokenizer())
    // test
    val htmlFormElement = Form.create(group,
      4,
      InputDefinition(
        "input_id",
        "label",
        value = { bean -> (bean as Group).name },
        save = { bean, element -> (bean as Group).name = (element as HTMLInputElement).value }),
      InputDefinition(
        "input_id2",
        "label",
        value = { bean -> (bean as Group).name },
        save = { bean, element -> (bean as Group).name = (element as HTMLInputElement).value })
    )

}
object Form {

    fun create(
      type: FormType = FormType.DEFAULT
    ): HTMLFormElement {
        val result = createTag("form") as HTMLFormElement

        result.cls(type.cls)

        return result
    }

    fun create(
      bean: Any,
      labelWidth: Int,
      vararg inputs: InputDefinition
    ): HTMLFormElement {
        val result = createTag("form") as HTMLFormElement

        for (input in inputs) {
            when(input.type) {
                InputType.TEXT -> {
                    result.add {
                        Input.create(
                          input.id,
                          label = input.label,
                          labelWidth = 4,
                          value = input.value(bean)) { e ->
                            input.save(bean, e.target as HTMLInputElement)
                        }
                    }
                }
            }
        }

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
      messages: List<String>? = null,
      blur: (Event) -> Unit = {},
      change: (Event) -> Unit = {}) : Element {
        val result = div().cls("form-group")

        if (messages != null && messages.isNotEmpty()) {
            result.cls("has-error")
        }

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

        input.on("blur", true, blur)

        input.on("keyup", true, change)

        result.add {
            div().cls("col-md-$inputWidth").add {
                input
            }
        }

        if (helpText.isNotBlank()) {
            result.add {
                createTag("div").cls("col-md-offset-$labelWidth col-md-$inputWidth").add {
                    createTag("span").cls("help-block").txt(helpText)
                }
            }
        }

        if (messages != null && messages.isNotEmpty()) {
            result.add {
                createTag("div").cls("col-md-offset-$labelWidth col-md-$inputWidth").add {
                    createTag("span").cls("help-block").txt(messages.joinToString("<br/>"))
                }
            }
        }

        return result
    }

}

object TextArea {

    fun create(
      id: String = "",
      name: String = id,
      label: String = "",
      labelWidth: Int,
      inputWidth: Int = (12 - labelWidth),
      value: String = "",
      placeHolder: String = "",
      helpText: String = "",
      classes: String = "",
      messages: List<String>? = null,
      change: (Event) -> Unit = {}) : Element {
        val result = div().cls("form-group")

        if (label.isNotBlank()) {
            result.with(createTag("label").attr("for", id).txt(label).cls("col-md-$labelWidth"))
        }

        val input = createTag("textarea")
          .attr("id", id)
          .cls("form-control $classes")
          .txt(value)

        if (placeHolder.isNotBlank()) {
            input.attr("placeholder", placeHolder)
        }

        input.on("keyup", true, change)

        result.add {
            div().cls("col-md-$inputWidth").add {
                input
            }
        }

        if (helpText.isNotBlank()) {
            result.add {
                createTag("div").cls("col-md-offset-$labelWidth col-md-$inputWidth").add {
                    createTag("span").cls("help-block").txt(helpText)
                }
            }
        }

        if (messages != null && messages.isNotEmpty()) {
            result.add {
                createTag("div").cls("col-md-offset-$labelWidth col-md-$inputWidth").add {
                    createTag("span").cls("help-block").txt(messages.joinToString("<br/>"))
                }
            }
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

object Checkbox {
    fun create(
      id: String,
      label: String,
      labelWidth: Int,
      inputWidth: Int,
      checked: Boolean = false,
      click: (HTMLInputElement) -> Unit = {}
    ): Element {
        return div().cls("form-group").add {
            div().cls("col-sm-offset-$labelWidth col-sm-$inputWidth").add {
                div().cls("checkbox").add {
                    val labelElem = createTag("label").addFirst {
                        val input = createTag("input")
                          .attr("type", "checkbox")

                        if (checked) {
                            input.attr("checked", "checked")
                        }

                        input.onClick { it ->
                            val target = it.target
                            if (target is HTMLInputElement) {
                                click(target)
                            }
                        }

                        input
                    }.add {
                        createTag("span").txt(label)
                    }

                    labelElem
                }
            }
        }
    }
}