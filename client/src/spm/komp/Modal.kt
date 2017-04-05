package stats.view

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.HtmlComponent
import nl.astraeus.komp.Komp
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import kotlin.browser.document

/**
 * Created by rnentjes on 30-3-17.
 */

interface OkCancelListener {

    fun ok() : Boolean

    fun cancel()

}

class ModalComponent(
  val modalId: String,
  val modalTitle: String,
  val body: HtmlComponent,
  val okText: String = "Ok",
  val cancelText: String = "Cancel",
  val okButtonClass: String = "btn-primary",
  val modalSize: String = "",
  var ok: () -> Unit = {},
  var cancel: () -> Unit = {}) : HtmlComponent() {

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "modal fade") {
        id = modalId
        tabIndex = "1"
        role = "dialog"
        div(classes = "modal-dialog $modalSize") {
            role = "document"
            div(classes = "modal-content") {
                div(classes = "modal-header") {
                    button(classes = "close") {
                        type = ButtonType.button
                        attributes["data-dismiss"] = "modal"
                        attributes["aria-label"] = "Close"
                        span {
                            attributes["aria-hidden"] = "true"
                            +"×"
                        }
                    }
                    h4(classes = "modal-title") { +modalTitle }
                }
                div(classes = "modal-body") {
                    include(body)
                }
                div(classes = "modal-footer") {
                    button(classes = "btn btn-default") {
                        type = ButtonType.button
                        attributes["data-dismiss"] = "modal"
                        +cancelText
                        onClickFunction = {
                            cancel()
                        }
                    }
                    button(classes = "btn $okButtonClass") {
                        type = ButtonType.button
                        +okText
                        onClickFunction = {
                            ok()
                        }
                    }
                }
            }
        }
    }
}

object Modal {
    var id = 0

    fun nextId() = "modal_${++id}"

    fun openModal(
      title: String,
      body: HtmlComponent,
      okText: String = "Ok",
      cancelText: String = "Cancel",
      modalSize: String = "",
      okButtonClass: String = "btn-primary",
      ok: () -> Boolean,
      cancel: () -> Unit = {}
    ): String {
        val id = nextId()
        val modal = ModalComponent(id, title, body, okText = okText, cancelText = cancelText, modalSize = modalSize, okButtonClass = okButtonClass, cancel = cancel)

        modal.ok = {
            if (ok()) {
                hideModal(id)
            }
        }

        Komp.create(document.body ?: throw IllegalStateException("Document.body not found!"), modal)

        attachHideEvent(id, Komp)

        showModal(id)

        return id
    }

    private fun attachHideEvent(id: String, context: dynamic) {
        js("""
            $('#' + id).on('hidden.bs.modal', function (event) {
                var element = event.currentTarget
                context.remove(element)
                element.parentElement.removeChild(element)
            })
        """)
    }

    fun hideModal(id: String) = js("$('#' + id).modal('hide')")

    fun showModal(id: String) = js("$('#' + id).modal('show')")
}
