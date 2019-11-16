package spm.view

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import spm.main
import spm.mainComponent
import spm.model.Group
import spm.state.UserState
import spm.ws.WebSocketConnection

class GroupCommands(
    val cg: Group,
    val refreshContainer: Komponent
) : Komponent() {

  fun rename(group: Group) {
    WebSocketConnection.lock { ws, tk ->
      val response = tk.next()

      if (response == "LOCKED") {
        val renameSubgroup = GroupNameEdit(group.name)
        UserState.readOnly = false
        UserState.obtainedLock = true
        mainComponent.refresh()
        Modal.openModal(
            "Add group",
            renameSubgroup,
            okText = "Save",
            okButtonClass = "btn-success",
            ok = {
              if (renameSubgroup.groupname.isBlank()) {
                //Notify.show("Group name can not be blank!", "error")
                Modal.showAlert("Error", "Group name can not be blank")
              } else {
                group.name = renameSubgroup.groupname
                UserState.saveData()
                refreshContainer.refresh()
              }

              true
            },
            cancel = {
              ws.send("UNLOCK")
            }
        )
      } else {
        Modal.showAlert("Blocked", "Unable to obtain modify lock.")
      }
    }
  }

  fun addSubgroup(group: Group) {
    WebSocketConnection.lock { ws, tk ->
      val response = tk.next()

      if (response == "LOCKED") {
        val addSubgroup = GroupNameEdit()
        Modal.openModal(
            "Add group",
            addSubgroup,
            okText = "Save",
            okButtonClass = "btn-success",
            ok = {
              if (addSubgroup.groupname.isBlank()) {
                //Notify.show("Group name can not be blank!", "error")
                Modal.showAlert("Error", "Group name can not be blank")
              } else {
                val newGroup = Group(addSubgroup.groupname, group)
                group.children.add(newGroup)

                UserState.saveData()
                refreshContainer.refresh()
              }

              true
            },
            cancel = {
              ws.send("UNLOCK")
            }
        )
      } else {
        Modal.showAlert("Blocked", "Unable to obtain modify lock.")
      }
    }
  }

  fun removeGroup(group: Group) {
    WebSocketConnection.lock { ws, tk ->
      val response = tk.next()

      if (response == "LOCKED") {
        val removeSubGroup = RemoveGroupConfirm(group.name)
        Modal.openModal(
            "Remove group",
            removeSubGroup,
            okText = "Remove",
            okButtonClass = "btn-danger",
            ok = {
              group.parent?.children?.remove(group)

              UserState.saveData()
              refreshContainer.refresh()

              true
            },
            cancel = {
              ws.send("UNLOCK")
            }
        )
      } else {
        Modal.showAlert("Blocked", "Unable to obtain modify lock.")
      }
    }
  }

  override fun render(consumer: HtmlBuilder) = consumer.div(classes = "col-md-6") {
    style = "margin-top: 20px;"
    a(classes = "btn btn-success btn-sm") {
      +"Rename"
      onClickFunction = {
        rename(cg)
      }
    }
    a(classes = "btn btn-primary btn-sm") {
      style = "margin-left:5px;"
      +"Add subgroup"
      onClickFunction = {
        addSubgroup(cg)
      }
    }
    a(classes = "btn btn-danger btn-sm") {
      style = "margin-left:5px;"
      if (cg.children.isNotEmpty() || cg.passwords.isNotEmpty() || cg.parent == null) {
        attributes["disabled"] = "disabled"
      }
      +"Remove group"
      onClickFunction = {
        if (cg.children.isEmpty() && cg.passwords.isEmpty() && cg.parent != null) {
          removeGroup(cg)
        }
      }
    }
  }
}
