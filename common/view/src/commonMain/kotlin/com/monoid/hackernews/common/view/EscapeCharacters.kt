package com.monoid.hackernews.common.view

private val escapedRegex = """&([^;]+);""".toRegex(RegexOption.IGNORE_CASE)

internal fun String.escapeCharacters(): String = buildString {
    var i = 0
    var match = escapedRegex.find(this@escapeCharacters, i)
    while (match != null) {
        append(this@escapeCharacters.subSequence(i, match.range.first))
        val m = match.groups[1]!!.value
        val c = if (m.startsWith("#")) {
            m.substring(1).toIntOrNull()?.toChar()
        } else {
            ESCAPE_MAP[m]
        }
        if (c != null) {
            append(c)
        } else {
            append(match.value)
        }
        i = match.range.last + 1
        match = match.next()
    }
    if (i < this@escapeCharacters.length) {
        append(this@escapeCharacters.subSequence(i, this@escapeCharacters.length))
    }
}
