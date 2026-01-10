package com.monoid.hackernews.common.view.stories

actual fun PlatformContext.displayMessage(message: String) {
    // TODO: display message
}

@JvmInline
actual value class PlatformContext(val platformContext: Any)
