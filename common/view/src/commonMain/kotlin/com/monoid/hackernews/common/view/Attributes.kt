package com.monoid.hackernews.common.view

fun List<TagToken>.toAttributes(): List<String>? {
    var map: MutableList<String>? = null
    var i = 0
    @Suppress("LoopWithTooManyJumpStatements")
    while (i < this@toAttributes.size - 2) {
        if (this@toAttributes[i + 1] != TagToken.Equal) {
            i++
            continue
        }
        val key = when (val k = this@toAttributes[0]) {
            TagToken.Equal -> {
                i++
                continue
            }

            is TagToken.Quote -> {
                k.tag
            }

            is TagToken.Word -> {
                k.word
            }
        }
        val value = when (val k = this@toAttributes[2]) {
            TagToken.Equal -> {
                i++
                continue
            }

            is TagToken.Quote -> {
                k.tag
            }

            is TagToken.Word -> {
                k.word
            }
        }
        if (map == null) {
            map = mutableListOf()
        }
        map.add(key)
        map.add(value)
        i += 3
    }
    return map
}

internal fun List<String>.lookup(key: String): String? {
    var i = 0
    while (i < size) {
        if (key.equals(this[i], ignoreCase = true)) {
            return this[i + 1]
        }
        i += 2
    }
    return null
}
