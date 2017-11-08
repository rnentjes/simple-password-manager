package spm.util

import kotlin.js.Date

fun Date.formatted(): String {
    val result = StringBuilder()
    val date = this
    val month = "${js("date.getMonth() + 1")}"
    val day = "${js("date.getDate()")}"
    val hour = "${js("date.getHours()")}"
    val minute = "${js("date.getMinutes()")}"

    result.append("${js("date.getFullYear()")}")
    result.append("-")
    result.append(if (month.length == 1) { "0$month" } else { month })
    result.append("-")
    result.append(if (day.length == 1) { "0$day" } else { day })

    result.append(" ")
    result.append(if (hour.length == 1) { "0$hour" } else { hour })
    result.append(":")
    result.append(if (minute.length == 1) { "0$minute" } else { minute })

    return result.toString()
}
