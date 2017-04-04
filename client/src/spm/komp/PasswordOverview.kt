package spm.komp

import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import nl.astraeus.komp.HtmlComponent
import org.w3c.dom.HTMLElement
import spm.state.UserState

/**
 * Created by rnentjes on 4-4-17.
 */

class PasswordOverview(val container: HtmlComponent) : HtmlComponent() {

    fun rename() { }

    fun addSubgroup() { }

    fun removeGroup() { }

    // language=html
    val html = """
        <a class="btn btn-success btn-sm col-md-2">Save name</a>
        <a class="btn btn-primary btn-sm col-md-2" style="margin-left:5px;">Add subgroup</a>
        <a class="btn btn-danger btn-sm col-md-2" style="margin-left:5px;" disabled="disabled">Remove group</a></div>
        """
    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.div(classes = "col-md-9") {
        div(classes = "row") {
            div(classes = "col-md-6") {
                h4 {
                    val group = UserState.currentGroup
                    if (group != null) {
                        + "Group ${group.name}"
                    }
                }
            }
            div(classes = "col-md-6") {
                a(classes = "btn btn-success btn-sm") {
                    + "Rename"
                    onClickFunction = {
                        rename()
                    }
                }
                a(classes = "btn btn-primary btn-sm") {
                    style = "margin-left:5px;"
                    + "Add subgroup"
                    onClickFunction = {
                        addSubgroup()
                    }
                }
                a(classes = "btn btn-danger btn-sm") {
                    style = "margin-left:5px;"
                    attributes["disabled"] = "disabled"
                    + "Remove group"
                    onClickFunction = {
                        removeGroup()
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
                        a(classes = "btn btn-success btn-sm") {
                            + "Add"
                            onClickFunction = {

                            }
                        }

                    }
                }
                h4 {
                    + "Passwords"
                }
            }
            div(classes = "row") {
                table(classes = "table table-striped table-condensed table-hover") {
                    tr {
                        td { + "Title" }
                        td { + "Url" }
                        td { + "Username" }
                        td { + "" }
                        td { + "" }
                        td { + "" }
                        td { + "" }
                    }
                }

            }
        }
    }

}