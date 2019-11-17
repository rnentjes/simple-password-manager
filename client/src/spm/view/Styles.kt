package spm.view

import nl.astraeus.komp.CssStyle

object Styles {
  private val primaryColor = "#eeeeee"

  val color: CssStyle = {
    color = primaryColor
  }

  val font: CssStyle = {
    fontFamily = "Arial, courier"
  }

  val selected: CssStyle = {
    color = "#444444"
    backgroundColor = "#FFEB91"
  }

  val found: CssStyle = {
      backgroundColor = "#FFEB91"
  }

  val nowrap: CssStyle = {
    whiteSpace ="nowrap"
  }
}
