package stats.view

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLElement
import kotlin.browser.document

/**
 * Created by rnentjes on 30-3-17.
 */

class ModalComponent(
  val modalId: String,
  val modalTitle: String,
  val body: Komponent,
  val okText: String = "Ok",
  val cancelText: String = "Cancel",
  val okButtonClass: String = "btn-primary",
  val modalSize: String = "",
  val showCancel: Boolean = true,
  val disabledOk: Boolean = false,
  var ok: () -> Unit = {},
  var cancel: () -> Unit = {}) : Komponent() {

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
                    h4(classes = "modal-title") { + modalTitle }
                }
                div(classes = "modal-body") {
                    include(body)
                }
                div(classes = "modal-footer") {
                    if (showCancel) {
                        button(classes = "btn btn-default") {
                            type = ButtonType.button
                            attributes["data-dismiss"] = "modal"
                            +cancelText
                            onClickFunction = { e ->
                                e.preventDefault()

                                cancel()
                            }
                        }
                    }
                    button(classes = "btn $okButtonClass") {
                        type = ButtonType.button
                        +okText
                        disabled = disabledOk
                        onClickFunction = { e ->
                            e.preventDefault()

                            ok()
                        }
                    }
                }
            }
        }
    }
}

class AlertComponent(val message: String): Komponent() {

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div {
        println("render AlertComponent $message")
        span {
            + message
        }
    }

}

object Modal {
    var id = 0

    fun nextId() = "modal_${++id}"

    fun openModal(
      title: String,
      body: Komponent,
      okText: String = "Ok",
      cancelText: String = "Cancel",
      modalSize: String = "",
      okButtonClass: String = "btn-primary",
      showCancel: Boolean = false,
      disabledOk: Boolean = false,
      ok: () -> Boolean,
      cancel: () -> Unit = {}
    ): String {
        val id = nextId()
        val modal = ModalComponent(
          id,
          title,
          body,
          okText = okText,
          cancelText = cancelText,
          modalSize = modalSize,
          okButtonClass = okButtonClass,
          showCancel = showCancel,
          disabledOk = disabledOk,
          cancel = cancel
        )

        modal.ok = {
            if (ok()) {
                hideModal(id)
            }
        }

        Komponent.create(document.body ?: throw IllegalStateException("Document.body not found!"), modal)

        attachHideEvent(id, Komponent)

        showModal(id)

        return id
    }

    fun showAlert(title: String, message: String, buttonText: String = "Close") {
        openModal(title, AlertComponent(message), showCancel = false, okText = buttonText, ok = { true })
    }

    fun showConfirm(
      title: String,
      body: Komponent,
      confirmText: String = "Yes",
      denyText: String = "No",
      showCancel: Boolean = false,
      disabledConfirm: Boolean = false,
      confirm: () -> Boolean = { true }
    ) {
        openModal(title, body, okText = confirmText, cancelText = denyText, disabledOk = disabledConfirm, ok = { confirm(); }, showCancel = showCancel)
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

    fun showModal(id: String) = js("$('#' + id).modal({ backdrop: 'static', keyboard: false })")
}
