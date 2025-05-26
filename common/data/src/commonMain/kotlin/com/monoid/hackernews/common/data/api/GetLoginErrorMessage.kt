package com.monoid.hackernews.common.data.api

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

fun getLoginErrorMessage(content: String): String {
    var body = false
    var message = "Login Error"
    val ksoupHtmlParser = KsoupHtmlParser(
        KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, _, _ ->
                if (name == "body") {
                    body = true
                }
            }
            .onText { text ->
                if (body) {
                    message = text
                    body = false
                }
            }
            .build(),
    )
    ksoupHtmlParser.write(content)
    ksoupHtmlParser.end()
    return message
}
