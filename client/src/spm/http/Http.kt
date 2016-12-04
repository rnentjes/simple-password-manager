package spm.http

import org.w3c.xhr.XMLHttpRequest

/**
 * Created by rnentjes on 20-11-16.
 */

object Http {

    fun readAsString(url: String, setter: (String) -> Unit) {
        val xmlHttp = XMLHttpRequest()

        xmlHttp.onreadystatechange = {
            if (xmlHttp.readyState == 4.toShort()) {
                if (xmlHttp.status == 200.toShort()) {
                    setter(xmlHttp.responseText)
                } else {
                    throw IllegalStateException("Couldn't load $url, http code ${xmlHttp.status} ${xmlHttp.responseText}")
                }
            }
        }

        xmlHttp.open("GET", url)
        xmlHttp.send()
    }
}
