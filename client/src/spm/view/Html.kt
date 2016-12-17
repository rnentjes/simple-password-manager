package spm.view

import org.w3c.dom.Element
import org.w3c.dom.HTMLTextAreaElement
import kotlin.browser.document
import kotlin.dom.addClass

/**
 * Created by rnentjes on 20-11-16.
 */


fun createTag(tag: String) = document.createElement(tag)

fun clear(elemId: String) {
    val outerDiv = elem(elemId)

    clear(outerDiv)
}

fun clear(elem: Element) {
    while(elem.children.length > 0) {
        elem.removeChild(elem.firstChild!!)
    }

    elem.innerHTML = ""
}

fun hasElem(id: String) = document.getElementById(id) != null
fun elem(id: String) = document.getElementById(id) ?: throw IllegalArgumentException("Element with id: '$id' not found!")
fun div() = createTag("div")

fun Element.attr(name: String, value: String): Element {
    this.setAttribute(name, value)

    return this
}

fun Element.with(elem: Element): Element {
    this.appendChild(elem)

    return this
}

fun Element.add(func: () -> Element): Element {
    this.appendChild(func())

    return this
}

fun Element.addFirst(func: () -> Element): Element {
    if (this.childElementCount > 0) {
        this.insertBefore(func(), this.firstChild)
    } else {
        this.append(func())
    }

    return this
}

fun Element.cls(cls: String): Element {
    this.addClass(cls)

    return this
}

fun Element.txt(txt: String): Element {
    this.innerHTML = txt

    return this
}

fun copyToClipboard(text: String) {
    val ta = createTag("textarea").txt(text)

    if (ta is HTMLTextAreaElement) {
        val body = document.body ?: throw IllegalStateException("The body was not found!")

        body.append(ta)
        ta.select()
        document.execCommand("copy")
        body.removeChild(ta)
    }
}

fun header(txt: String): Element {
    return createTag("div").cls("page-header").add {
        createTag("h4").txt(txt)
    }
}
