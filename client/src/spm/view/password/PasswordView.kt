package spm.view.password

/**
 * User: rnentjes
 * Date: 27-11-16
 * Time: 16:20
 */


data class Password(
  var id: Long,
  var name: String,
  var parent: spm.view.group.Group?
)