package spm.view.input

import kotlinx.html.*
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onKeyUpFunction
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

/**
 * User: rnentjes
 * Date: 2-8-17
 * Time: 10:15
 */

class TextInput(
  var inputId: String,
  var label: String = "",
  var inputValue: String = "",
  var inputType: InputType = InputType.text,
  var placeholderText: String = "",
  var error: String = "",
  var blur: (Event) -> Unit = {},
  var change: (Event) -> Unit = {}
): Komponent() {

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "form-group") {
        if (error.isNotBlank()) {
            classes += "has-error"
        }
        if (label.isNotBlank()) {
            label(classes = "col-md-3") {
                for_ = inputId
                +label
            }
        }
        div(classes = "col-md-9") {
            input(classes = "form-control") {
                id = inputId
                name = inputId
                type = InputType.text
                value = inputValue
            }
            if (placeholderText.isNotBlank()) {
                attributes["placeholder"] = placeholderText
            }
            if (error.isNotBlank()) {
                span(classes = "help-block") {
                    +error
                }
            }
        }

        onBlurFunction = blur
        onKeyUpFunction = change
    }
}
