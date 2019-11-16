package spm.view

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent
import spm.model.Group
import spm.state.UserState

class GroupCommands(
    val cg: Group,
    val refreshContainer: Komponent
): Komponent() {

  fun rename(group: Group) {
    val renameSubgroup = GroupNameEdit(group.name)
    Modal.openModal("Add group", renameSubgroup, okText = "Save", okButtonClass = "btn-success", ok = {

      if (renameSubgroup.groupname.isBlank()) {
        //Notify.show("Group name can not be blank!", "error")
        Modal.showAlert("Error", "Group name can not be blank")
      } else {
        group.name = renameSubgroup.groupname
        UserState.saveData()
        refreshContainer.refresh()
      }

      true
    })
  }

  fun addSubgroup(group: Group) {
    val addSubgroup = GroupNameEdit()
    Modal.openModal("Add group", addSubgroup, okText = "Save", okButtonClass = "btn-success", ok = {

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
    })
  }

  fun removeGroup(group: Group) {
    val removeSubGroup = RemoveGroupConfirm(group.name)
    Modal.openModal("Remove group", removeSubGroup, okText = "Remove", okButtonClass = "btn-danger", ok = {
      group.parent?.children?.remove(group)

      UserState.saveData()
      refreshContainer.refresh()

      true
    })
  }

  override fun render(consumer: HtmlBuilder) =  consumer.div(classes = "col-md-6") {
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
