package spm.view.button

import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.Komponent
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

/**
 * User: rnentjes
 * Date: 11-9-17
 * Time: 20:18
 */

class PasswordButton(
   val icon: String,
   val text: String = "",
   val tooltip: String? = null,
   val buttonStyle: String = "",
   val btnClass: String = "btn-default",
   val click: (Event) -> Unit = {}
): Komponent() {

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.button(classes = "btn $btnClass") {
        type = ButtonType.button
        if (buttonStyle.isNotBlank()) {
            style = buttonStyle
        }
        attributes["aria-label"] = text
        if (tooltip != null) {
            title = tooltip
        }

        + text

        if (icon.isNotBlank()) {
            span(classes = "glyphicon glyphicon-$icon") {
                attributes["aria-hidden"] = "true"
            }
        }

        onClickFunction = click
    }

}
