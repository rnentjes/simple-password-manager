package spm.util

import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.title
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 8-11-17
 * Time: 17:44
 */

fun trimmed(consumer: TagConsumer<HTMLElement>, str: String, length: Int) = consumer.td {
    if (str.length > length) {
        title = str
        + "${str.substring(0 until length - 3)}..."
    } else {
        + str
    }
}

fun copyToClipboard(
  text: String,
  parentToAppendTo: HTMLElement = document.body ?: throw IllegalStateException("The body was not found!")
) {
    val ta = document.createElement("textarea")
    ta.innerHTML = text

    if (ta is HTMLTextAreaElement) {
        parentToAppendTo.appendChild(ta)
        ta.select()
        document.execCommand("copy")
        parentToAppendTo.removeChild(ta)
    } else {
        throw IllegalStateException("Created element isn't HTMLTextAreaElement but $ta")
    }
}
