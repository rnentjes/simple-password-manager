package spm.view

import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.dom.addClass

/**
 * Created by rnentjes on 20-11-16.
 */


fun createTag(tag: String) = document.createElement(tag)

fun clear(elemId: String) {
    val outerDiv = elem(elemId)

    while(outerDiv.children.length > 0) {
        outerDiv.removeChild(outerDiv.firstChild!!)
    }
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

fun Element.cls(cls: String): Element {
    this.addClass(cls)

    return this
}

fun Element.txt(txt: String): Element {
    this.innerHTML = txt

    return this
}