package spm.view.login

import org.w3c.dom.Element
import spm.view.*
import spm.view.form.*
import spm.view.main.MainView

/**
 * Created by rnentjes on 20-11-16.
 */

object LoginView {
    //language=HTML
    var loginHtml = """
  <div class="row">
    <div class="col-md-6 col-lg-offset-3 col-sm-6 col-sm-offset-3 col-xs-12">
      <form class="form-signin">
        <h2 class="form-signin-heading">Please sign in</h2>

        <label for="inputEmail" class="sr-only">Email address</label>
        <input type="email" id="inputEmail" class="form-control" placeholder="Email address" required autofocus>
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" id="inputPassword" class="form-control" placeholder="Password" required>
        <div class="checkbox">
          <label>
            <input type="checkbox" value="remember-me"> Remember me
          </label>
        </div>
        <a href="#" class="btn btn-lg btn-primary btn-block" id="login_submit">Sign in</a>
      </form>
    </div>
  </div>
    """

    fun create(): Element {
        val result = div()

        result.setAttribute("class", "container")

        val div = div().cls("col-md-5 col-md-offset-3")

        div.with(Form.create(FormType.HORIZONTAL).with(
              Input.create("login_name", label = "Login name", labelWidth = 4)
            ).with(
              Input.create("password", type = "password", label = "Password", labelWidth = 4)
            ).with(
              FormLinkButton.create("Login", buttonClass = "btn-primary", labelWidth = 4, click = {
                  showMainView(result)
              })
            )
        )

        result.with(createTag("h1").txt("Login")).with(div().cls("row").with(div))

        return result
    }

    private fun showMainView(parent: Element) {
        while(parent.children.length > 0) {
            parent.removeChild(parent.firstChild!!)
        }

        parent.with(MainView.create())
    }

}
