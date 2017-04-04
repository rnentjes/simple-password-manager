package spm.komp

import kotlinx.html.*
import kotlinx.html.js.nav
import nl.astraeus.komp.HtmlComponent
import org.w3c.dom.HTMLElement

/**
 * Created by rnentjes on 3-4-17.
 */
class Navbar: HtmlComponent() {

    override fun render(consumer: TagConsumer<HTMLElement>) = consumer.nav(classes="navbar navbar-default navbar-static-top") {
        div(classes = "container-fluid") {
            unsafe {
                //language=html
                +"""
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand" href="#">Simple password manager</a>
                    </div>
                """
            }
            div(classes = "collapse navbar-collapse") {
                id = "bs-example-navbar-collapse-1"
                ul(classes = "nav navbar-nav navbar-right") {
                    li {
                        a {
                            href = "#"
                            + "Logout"
                        }
                    }
                }
                form(classes = "navbar-form navbar-right") {
                    div(classes = "form-group") {
                        input(classes = "form-control") {
                            type = InputType.text
                            placeholder = "Search"
                        }
                    }
                    button(classes = "btn btn-default") {
                        type = ButtonType.submit
                        + "Search"
                    }
                }
            }
        }
    }

}
