package nl.astraeus.spm.web

import fi.iki.elonen.NanoHTTPD

/**
 * Created by rnentjes on 24-11-16.
 */

fun handleInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Hello v0.1")
}

val handlers = mapOf(
  "/info" to ::handleInfo
)
