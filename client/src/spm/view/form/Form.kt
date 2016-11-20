package spm.view.form

import org.w3c.dom.Element
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.events.Event
import spm.view.*
import kotlin.dom.on

/**
 * Created by rnentjes on 20-11-16.
 */

enum class FormType(val cls: String) {
    DEFAULT(""),
    INLINE("form-inline"),
    HORIZONTAL("form-horizontal")
}

enum class GridWith(val labelWidth: String, val inputWidth: String) {
    COL_1("col-md-1", "col-md-11"),
    COL_2("col-md-2", "col-md-10"),
    COL_3("col-md-3", "col-md-9"),
    COL_4("col-md-4", "col-md-8"),
    COL_5("col-md-5", "col-md-7"),
    COL_6("col-md-6", "col-md-6"),
    COL_7("col-md-7", "col-md-5"),
    COL_8("col-md-8", "col-md-4"),
    COL_9("col-md-9", "col-md-3"),
    COL_10("col-md-10", "col-md-2"),
    COL_11("col-md-11", "col-md-1")
}

object Form {

    fun create(
      type: FormType = FormType.DEFAULT,
      labelWidth: GridWith = GridWith.COL_2
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
      labelWidth: GridWith = GridWith.COL_2,
      value: String = "",
      placeHolder: String = "",
      change: (Event) -> Unit = {}) : Element {
        val result = div().cls("form-group")

        if (label.isNotBlank()) {
            result.with(createTag("label").attr("for", id).txt(label).cls(labelWidth.labelWidth))
        }

        val input = createTag("input")
          .attr("id", id)
          .attr("type", type)
          .attr("value", value)
          .cls("form-control")

        if (placeHolder.isNotBlank()) {
            input.attr("placeholder", placeHolder)
        }

        input.on("change", true, change)

        result.with(div().cls(labelWidth.inputWidth).with(input))

        return result
    }

}
