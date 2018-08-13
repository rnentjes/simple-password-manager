package spm.view

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h3
import kotlinx.html.h4
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import kotlinx.html.label
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import nl.astraeus.komp.KompConsumer
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import spm.model.Group
import spm.model.Password
import spm.state.UserState
import kotlin.browser.document

/**
 * Created by rnentjes on 4-4-17.
 */

class RemovePasswordConfirm(val password: Password) : Komponent() {
  override fun render(consumer: KompConsumer) = consumer.span {
    +"Are you sure you want to remove password '${password.title}'?"
  }
}

class RemoveGroupConfirm(val groupName: String) : Komponent() {
  override fun render(consumer: KompConsumer) = consumer.span {
    +"Are you sure you want to remove group '$groupName'?"
  }
}

class GroupNameEdit(var groupname: String = "") : Komponent() {

  override fun render(consumer: KompConsumer) = consumer.div(classes = "") {
    form(classes = "form form-horizontal") {
      div(classes = "form-group") {
        label(classes = "col-md-3") {
          for_ = "groupname"
          +"Group name"
        }
        div(classes = "col-md-9") {
          input(classes = "form-control") {
            id = "groupname"
            value = groupname

            fun changeName(e: Event) {
              e.preventDefault()

              groupname = (e.target as HTMLInputElement).value
            }

            onBlurFunction = ::changeName
            onKeyUpFunction = ::changeName
          }
        }
      }

    }
  }
}

class PasswordOverview(val container: Komponent) : Komponent() {

  fun rename(event: Event) {
    val group = UserState.currentGroup

    if (group != null) {
      val renameSubgroup = GroupNameEdit(group.name)
      Modal.openModal("Add group", renameSubgroup, okText = "Save", okButtonClass = "btn-success", ok = {

        if (renameSubgroup.groupname.isBlank()) {
          //Notify.show("Group name can not be blank!", "error")
          Modal.showAlert("Error", "Group name can not be blank")
        } else {
          group.name = renameSubgroup.groupname
          UserState.saveData()
          container.refresh()
        }

        true
      })
    }
  }

  fun addSubgroup(event: Event) {
    val group = UserState.currentGroup

    if (group != null) {
      val addSubgroup = GroupNameEdit()
      Modal.openModal("Add group", addSubgroup, okText = "Save", okButtonClass = "btn-success", ok = {

        if (addSubgroup.groupname.isBlank()) {
          //Notify.show("Group name can not be blank!", "error")
          Modal.showAlert("Error", "Group name can not be blank")
        } else {
          val newGroup = Group(addSubgroup.groupname, group)
          group.children.add(newGroup)

          UserState.saveData()
          container.refresh()
        }

        true
      })
    }
  }

  fun removeGroup(event: Event) {
    val group = UserState.currentGroup

    if (group != null) {
      if (group.children.isEmpty() && group.passwords.isEmpty() && group.parent != null) {
        val removeSubGroup = RemoveGroupConfirm(group.name)
        Modal.openModal("Remove group", removeSubGroup, okText = "Remove", okButtonClass = "btn-danger", ok = {
          group.parent?.children?.remove(group)

          UserState.saveData()
          container.refresh()

          true
        })
      }
    }
  }

  private fun renameGroup(event: Event) {
    val cg = UserState.currentGroup

    if (cg != null) {
    }
  }

  private fun addPassword(event: Event) {
    val cg = UserState.currentGroup

    if (cg != null) {
      val editor = PasswordEditor(cg)
      editor.password.group = cg
      Modal.openModal("Edit password",
          editor,
          okText = "Save",
          okButtonClass = "btn-success",
          ok = {
            if (editor.validate()) {
              if (editor.originalPassword == null) {
                editor.password.encryptedPassword = UserState.encryptPassword(editor.password.password1)
                editor.password.group.passwords.add(editor.password)
              } else {
                throw IllegalStateException("Add button modal has existing password!?")
              }
              UserState.saveData()
              container.refresh()

              true
            } else {

              false
            }
          })
    }
  }

  override fun render(consumer: KompConsumer) = consumer.div(classes = "col-md-9") {
    val cg = UserState.currentGroup
    //console.log("Currentgroup: ", cg)
    if (cg != null) {
      div(classes = "row") {
        div(classes = "col-md-6") {
          h3 {
            // background-color: #f8f8f8;
            style = "text-align: center; padding: 10px; margin: 5px"
            val group = UserState.currentGroup
            if (group != null) {
              +group.name
            }
          }
        }

        if (!UserState.readOnly) {
          div(classes = "col-md-6") {
            style = "margin-top: 20px;"
            a(classes = "btn btn-success btn-sm") {
              +"Rename"
              onClickFunction = this@PasswordOverview::rename
            }
            a(classes = "btn btn-primary btn-sm") {
              style = "margin-left:5px;"
              +"Add subgroup"
              onClickFunction = this@PasswordOverview::addSubgroup
            }
            a(classes = "btn btn-danger btn-sm") {
              style = "margin-left:5px;"
              if (cg.children.isNotEmpty() || cg.passwords.isNotEmpty() || cg.parent == null) {
                attributes["disabled"] = "disabled"
              }
              +"Remove group"
              onClickFunction = this@PasswordOverview::removeGroup
            }
          }
        }
      }
      div(classes = "row") {
        hr {}
      }
      div {
        //id = "passwords_overview"
        div(classes = "page-header") {
          div(classes = "btn-toolbar pull-right") {
            div(classes = "button-group") {
              if (!UserState.readOnly) {
                a(classes = "btn btn-success btn-sm") {
                  +"Add"
                  onClickFunction = this@PasswordOverview::addPassword
                }
              }
            }
          }
          h4 {
            +"Passwords"
          }
        }
        div(classes = "row") {
          table(classes = "table table-striped table-condensed table-hover") {
            tr {
              th { +"Title" }
              th { +"Url" }
              th { +"Username" }
              th { +"Hist" }
              th { +"" }
            }
            for (password in cg.passwords) {
              include(PasswordOverviewRow(password, container, this@PasswordOverview))
            }
          }
        }
      }
    }
  }

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
}