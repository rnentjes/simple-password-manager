package nl.astraeus.spm

import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class InfoServlet: HttpServlet() {

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")

    private fun formatDate(date: Date):String {
        return dateFormatter.format(date)
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if (resp != null) {
            resp.contentType = "text/html"
            resp.characterEncoding = "UTF-8"
            resp.setHeader("X-Content-Type-Options", "nosniff")

            var writer = resp.getWriter()

            writer.print("""
            <html><head><title>mto-info</title></head><body>
            """)

/*
            transaction {
                for (song in SongDao.all()) {
                    var patterns = PatternsDao.findBySong(song.songId)

                    if (patterns.size > 0) {
                        writer.println("<a href=\"https://www.music-tracker-online.com/tracker.html?song=${song.songId}&play=true\">${song.songId}, ${song.name}, ${song.user}, ${patterns.size}, ${dateFormatter.format(song.created)}</a><br/>")
                    }
                }
            }
*/
            writer.print("simple-password-manager v0.1")

            writer.print("""
            </body></html>
            """)

            writer.flush()
        }
    }
}