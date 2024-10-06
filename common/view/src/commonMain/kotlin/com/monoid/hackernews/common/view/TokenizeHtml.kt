package com.monoid.hackernews.common.view

internal val WHITESPACE_REGEX = """\s+""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_START_REGEX = """</?[^\s/>]+""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_WORD_REGEX = """[^="\s>]+""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_QUOTE_REGEX = """"([^"]*)"""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_EQUAL_REGEX = """=""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_END_REGEX = """/?>""".toRegex(RegexOption.IGNORE_CASE)
internal val WORD_REGEX = """[^<\s]+""".toRegex(RegexOption.IGNORE_CASE)

@Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
fun tokenizeHtml(htmlString: String): ArrayDeque<HtmlToken> {
    val tokens = ArrayDeque<HtmlToken>()
    var i = 0
    @Suppress("LoopWithTooManyJumpStatements")
    outer@ while (i < htmlString.length) {
        var match = WHITESPACE_REGEX.matchAt(htmlString, i)
        if (match != null) {
            i = match.range.last + 1
            tokens.add(HtmlToken.Whitespace(match.value))
            continue
        }
        match = TAG_START_REGEX.matchAt(htmlString, i)
        if (match != null) {
            var k = match.range.last + 1
            val start = match.value
            val tagTokens = mutableListOf<TagToken>()
            while (k < htmlString.length) {
                var tagMatch = WHITESPACE_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    continue
                }
                tagMatch = TAG_END_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagMatch.value.replace("&amp;", "&")
                    tokens.add(
                        HtmlToken.Tag(
                            start.lowercase(),
                            tagTokens,
                            tagMatch.value.lowercase()
                        )
                    )
                    i = k
                    continue@outer
                }
                tagMatch = TAG_EQUAL_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Equal)
                    continue
                }
                tagMatch = TAG_QUOTE_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Quote(tagMatch.groups[1]!!.value.escapeCharacters()))
                    continue
                }
                tagMatch = TAG_WORD_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Word(tagMatch.value))
                    continue
                }
            }
            // end of input found before end of tag so fall through
        }
        match = WORD_REGEX.matchAt(htmlString, i)
        if (match != null) {
            i = match.range.last + 1
            tokens.add(HtmlToken.Word(match.value.escapeCharacters()))
            continue
        }
        @Suppress("UseCheckOrError", "ThrowingExceptionsWithoutMessageOrCause")
        throw IllegalStateException("no matching regexes")
    }
    return tokens
}
