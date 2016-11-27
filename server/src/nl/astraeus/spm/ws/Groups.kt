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


fun updateGroupName(ws: SimpleWebSocket, tk: Tokenizer) {
    val id = tk.next().toLong()
    val name = tk.next()

    val group = GroupDao.find(id)

    if (group != null) {
        group.name = name

        GroupDao.update(group)
    } else {
        // send error
    }
}

fun sendGroups(ws: SimpleWebSocket, email: String) {
    for (group in GroupDao.findByUser(email)) {

    }

}
