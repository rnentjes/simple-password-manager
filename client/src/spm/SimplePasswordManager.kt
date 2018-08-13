package spm

import nl.astraeus.komp.Komponent
import nl.astraeus.komp.UpdateStrategy
import spm.model.Group
import spm.state.UserState
import spm.view.Main
import spm.ws.WebSocketConnection
import kotlin.browser.document
import kotlin.browser.window

/**
 * User: rnentjes
 * Date: 20-11-16
 * Time: 12:24
 */

val mainComponent = Main()

var first = true
var group1: Group? = null
var group2: Group? = null

fun runTest() {
  if (first) {
    UserState.currentGroup = group1
  } else {
    UserState.currentGroup = group2
  }

  mainComponent.update()

  first = !first

  window.setTimeout({
    runTest()
  }, 250)
}

fun test() {
  if (UserState.topGroup == null) {
    window.setTimeout({
      test()
    }, 1000)
  } else {
    group1 = UserState.topGroup?.findById(3)
    group2 = UserState.topGroup?.findById(4)

    runTest()
  }
}

fun main(args: Array<String>) {
  val splash = document.getElementById("splash")

  splash?.parentElement?.removeChild(splash)

  Komponent.logRenderEvent = true
  Komponent.logReplaceEvent = true
  Komponent.logEquals = true
  Komponent.updateStrategy = UpdateStrategy.DOM_DIFF

  Komponent.create(document.body!!, mainComponent)

  WebSocketConnection.open()

  //test()
}
