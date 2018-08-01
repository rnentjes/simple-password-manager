package spm.view.input

import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onChangeFunction
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.span
import nl.astraeus.komp.KompConsumer
import nl.astraeus.komp.Komponent
import org.w3c.dom.events.Event

/**
 * User: rnentjes
 * Date: 1-8-17
 * Time: 17:09
 */

class SelectInput(
  var inputId: String,
  var inputValue: String = "",
  var options: List<Pair<String, String>> = ArrayList(),
  var placeholderText: String = "",
  var error: String = "",
  var label: String = "",
  var readOnly: Boolean = false,
  var blur: (Event) -> Unit = {},
  var change: (Event) -> Unit = {}
): Komponent() {

    override fun render(consumer: KompConsumer) = consumer.div(classes = "form-group") {
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
            select(classes = "form-control") {
                id = inputId
                name = inputId
                readOnly = this@SelectInput.readOnly
                disabled = this@SelectInput.readOnly

                for (option in options) {
                    option {
                        value = option.first
                        if (value == inputValue) {
                            selected = true
                        }
                        + option.second
                    }
                }
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
        onChangeFunction = change
    }

}