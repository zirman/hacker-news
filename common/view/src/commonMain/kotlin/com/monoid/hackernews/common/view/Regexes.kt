package com.monoid.hackernews.common.view

internal val KEYWORD_COLOR_REGEX = """^[a-z]*\$""".toRegex(RegexOption.IGNORE_CASE)
internal val HEX_COLOR_REGEX = """^#[0-9a-f]{3}([0-9a-f]{3})?\$""".toRegex(RegexOption.IGNORE_CASE)

@Suppress("MaxLineLength")
internal val RGB_COLOR_REGEX =
    """^rgb(\s*(0|[1-9]\d?|1\d\d?|2[0-4]\d|25[0-5])%?\s*,\s*(0|[1-9]\d?|1\d\d?|2[0-4]\d|25[0-5])%?\s*,\s*(0|[1-9]\d?|1\d\d?|2[0-4]\d|25[0-5])%?\s*)\$"""
        .toRegex(RegexOption.IGNORE_CASE)

@Suppress("MaxLineLength")
internal val RGBA_COLOR_REGEX =
    """^rgba\(\s*(0|[1-9]\d?|1\d\d?|2[0-4]\d|25[0-5])%?\s*,\s*(0|[1-9]\d?|1\d\d?|2[0-4]\d|25[0-5])%?\s*,\s*(0|[1-9]\d?|1\d\d?|2[0-4]\d|25[0-5])%?\s*,\s*((0.[1-9])|[01])\s*\)\$"""
        .toRegex(RegexOption.IGNORE_CASE)

internal val HSL_COLOR_REGEX =
    """^hsl\(\s*(0|[1-9]\d?|[12]\d\d|3[0-5]\d)\s*,\s*((0|[1-9]\d?|100)%)\s*,\s*((0|[1-9]\d?|100)%)\s*\)\$}"""
        .toRegex(RegexOption.IGNORE_CASE)
