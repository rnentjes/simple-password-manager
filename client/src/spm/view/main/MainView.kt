package spm.view.main

import org.w3c.dom.Element
import spm.view.createTag
import spm.view.div
import spm.view.txt
import spm.view.with

/**
 * Created by rnentjes on 22-11-16.
 */

object MainView {

    fun create(): Element {

        return div().with(createTag("h1").txt("Main view!"))
    }
}