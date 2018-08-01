package spm.view

import kotlinx.html.div
import nl.astraeus.komp.KompConsumer
import nl.astraeus.komp.Komponent
import nl.astraeus.komp.include
import spm.state.UserState

/**
 * Created by rnentjes on 3-4-17.
 */

class Container(main: Komponent) : Komponent() {
  val navbar = Navbar(main, this)
  val groupOverview = GroupOverview(this)
  val passwordOverview = PasswordOverview(this)
  val searchResult = SearchResult(this)

  override fun render(consumer: KompConsumer) = consumer.div {
    include(navbar)

    div(classes = "container") {
      include(groupOverview)
      if (UserState.currentSearch.isBlank()) {
        include(passwordOverview)
      } else {
        include(searchResult)
      }
    }
  }

}
