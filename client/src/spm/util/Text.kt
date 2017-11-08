package spm.util

import org.w3c.dom.HTMLTextAreaElement
import kotlin.browser.document

/**
 * User: rnentjes
 * Date: 8-11-17
 * Time: 17:44
 */

fun copyToClipboard(text: String) {
    val ta = document.createElement("textarea")
    ta.innerHTML = text

    if (ta is HTMLTextAreaElement) {
        val body = document.body ?: throw IllegalStateException("The body was not found!")

        body.appendChild(ta)
        ta.select()
        document.execCommand("copy")
        body.removeChild(ta)
    }
}
