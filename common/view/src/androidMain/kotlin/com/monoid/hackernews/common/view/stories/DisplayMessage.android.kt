package com.monoid.hackernews.common.view.stories

import android.content.Context
import android.widget.Toast

actual fun PlatformContext.displayMessage(message: String) {
    Toast
        .makeText(
            context,
            message,
            Toast.LENGTH_SHORT,
        )
        .show()
}

@JvmInline
actual value class PlatformContext(val platformContext: Any) {
    val context: Context get() = platformContext as Context
}
