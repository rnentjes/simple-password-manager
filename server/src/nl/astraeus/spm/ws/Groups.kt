package nl.astraeus.spm.ws

import nl.astraeus.spm.model.GroupDao
import nl.astraeus.spm.util.Tokenizer
import nl.astraeus.spm.web.SimpleWebSocket

/**
 * User: rnentjes
 * Date: 23-11-16
 * Time: 11:32
 */

fun createGroup(ws: SimpleWebSocket, tk: Tokenizer) {

}

fun sendGroups(ws: SimpleWebSocket, email: String) {
    for (group in GroupDao.findByUser(email)) {

    }

}
