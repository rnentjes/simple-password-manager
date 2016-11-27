package nl.astraeus.spm.util

/**
 * User: rnentjes
 * Date: 10-2-16
 * Time: 10:57
 */

/**
 * read text as tokens
 * tokens are seperated by <seperator> e.g. ~
 * <seperator> is escaped by <escape> e.g. `~
 */
class Tokenizer(val txt: String = "", val seperator: Char = '~', val escape: Char = '`') {
    var index = 0

    fun done() = index >= txt.length

    fun next(): String {
        //println("Getting next $index/${txt.length} -> $txt")
        val result = StringBuilder()
        var escaped = false

        if (done()) {
            //println("Next is ''")

            return ""
        }

        while(!done()) {
            val ch = txt[index]

            if (escaped) {
                result.append(ch)
                escaped = false
            } else {
                when (ch) {
                    escape -> {
                        escaped = true
                    }
                    seperator -> {
                        index++
                        return result.toString()
                    }
                    else -> {
                        result.append(ch)
                    }
                }
            }

            index++
        }

        //println("Next is '$result'")
        return result.toString()
    }

    fun tokenize(vararg parts: String): String {
        val result = StringBuilder()

        for (part in parts) {
            if (result.isNotEmpty()) {
                result.append(seperator)
            }
            result.append(escape(part))
        }

        return result.toString()
    }

    override fun toString(): String{
        return "Tokenizer(index=$index, txt='$txt')"
    }

    fun escape(txt: String): String {
        val result = StringBuilder()

        for (index in 0..txt.length-1) {
            val ch = txt[index]

            when(ch) {
                escape -> {
                    result.append("$escape$escape")
                }
                seperator -> {
                    result.append("$escape$seperator")
                }
                else -> {
                    result.append(ch)
                }
            }
        }

        return result.toString()
    }

    companion object {
        fun tokenize(vararg parts: String): String {
            val tokenizer = Tokenizer()

            return tokenizer.tokenize(*parts)
        }
    }
}

fun deTokenize(str: String, indent: Int = 1, seperator: Char = '~', escape: Char = '`') {
    val t = Tokenizer(str, seperator, escape)
    var first = true

    while(!t.done()) {
        val next = t.next()
        for(count in 0..indent-1) {
            print("-")
        }
        println(">$next")

        if (!t.done() || !first) {
            deTokenize(next, indent+1, seperator, escape)
        }

        first = false
    }
}

fun main(args: Array<String>) {
    var seperator = '~'
    var escape = '`'
    var t = Tokenizer("", seperator, escape)

    var t1 = t.tokenize("hello", "sweet", "world :)")
    var t2 = t.tokenize("goodbuy", "cruel", "world :(")
    var t3 = t.tokenize(t1, t2)
    var t4 = t.tokenize(t2, t3)
    var t5 = t.tokenize(t1, t4)
    var t6 = t.tokenize(t5, t4)
    var t7 = t.tokenize(t6, t2)
    var t8 = t.tokenize(t5, t3)
    var t9 = t.tokenize(t7, t8)

    println("-> $t9")

    deTokenize(t9, 1, seperator, escape)
}
