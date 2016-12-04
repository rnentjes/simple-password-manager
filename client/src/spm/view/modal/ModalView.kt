package spm.view.modal

import org.w3c.dom.Element
import org.w3c.dom.events.Event
import spm.view.*
import kotlin.browser.document
import kotlin.dom.onClick

/**
 * Created by rnentjes on 27-11-16.
 */

object Notify {

    /**
     * type:
     * - success
     * - info
     * - warning
     * - error
     */
    fun show(message: String, type: String = "") {
        js("$.notify(message, type)")
    }

}

object ModalView {

    fun showAlert(title: String, message: String, buttonText: String = "Close") {
        showModal(
          div().cls("modal-content").add {
              div().cls("modal-header").add {
                  createTag("button").cls("close").attr("data-dismiss", "modal").attr("aria-label", "Close").add {
                      createTag("span").attr("aria-hidden", "true").txt("&times;")
                  }
              }.add {
                  createTag("h4").cls("modal-title").txt(title)
              }
          }.add {
              div().cls("modal-body").txt(message)
          }.add {
              div().cls("modal-footer").add {
                  createTag("button").cls("btn btn-default").attr("data-dismiss", "modal").txt(buttonText)
              }
          }
        )
    }

    fun showConfirm(
      title: String,
      body: Element,
      confirmText: String = "Yes",
      denyText: String = "No",
      disabledConfirm: Boolean = false,
      confirm: (Event) -> Unit = {}
    ) {
        showModal(
          div().cls("modal-content").add {
              div().cls("modal-header").add {
                  createTag("button").cls("close").attr("data-dismiss", "modal").attr("aria-label", "Close").add {
                      createTag("span").attr("aria-hidden", "true").txt("&times;")
                  }
              }.add {
                  createTag("h4").cls("modal-title").txt(title)
              }
          }.add {
              div().cls("modal-body").add { body }
          }.add {
              div().cls("modal-footer").add {
                  createTag("button").cls("btn btn-default").attr("data-dismiss", "modal").txt(denyText)
              }.add {
                  val confirmButton = createTag("button").attr("id", "modal_confirm_button").cls("btn btn-success").attr("data-dismiss", "modal").txt(confirmText)

                  if (disabledConfirm) {
                      confirmButton.attr("disabled", "disabled")
                  }

                  confirmButton.onClick { e -> confirm(e) }

                  confirmButton
              }
          }
        )
    }

    fun showModal(child: Element) {
        val parent = getModal()

        parent.add {
            div().cls("modal-dialog").attr("role", "document").add {
                child
            }
        }

        showModal()
    }

    fun hideModal() = js("$('#modal_element').modal('hide')")

    fun showModal() = js("$('#modal_element').modal('show')")

    private fun getModal(): Element {
        if (hasElem("modal_element")) {
            clear("modal_element")

            return elem("modal_element")
        } else {
            val modalElement = div().attr("id", "modal_element").attr("role", "dialog").cls("modal fade")

            val body = document.body ?: throw IllegalArgumentException("No document.body element found, is this even a browser!?")

            // insert modal as first element of the body
            if (body.childElementCount > 0) {
                body.insertBefore(modalElement, body.firstChild)
            } else {
                body.append(modalElement)
            }

            return modalElement
        }
    }
}