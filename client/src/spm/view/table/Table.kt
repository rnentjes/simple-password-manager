package spm.view.table

import kotlinx.html.Tag
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import nl.astraeus.komp.HtmlBuilder
import nl.astraeus.komp.Komponent

class Table<T>(
    val headers: Array<String>,
    val rows: List<T>,
    val rowRenderer: Tag.(T) -> Unit
): Komponent() {

  override fun render(consumer: HtmlBuilder) = consumer.table(classes = "table table-condensed table-hover") {
      thead {
        tr {
          for(header in headers) {
            th { +header }
          }
        }
      }
      tbody {
        for (row in rows) {
          rowRenderer(this, row)
        }
      }
  }

}
