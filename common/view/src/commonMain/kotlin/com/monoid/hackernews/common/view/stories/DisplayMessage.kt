package com.monoid.hackernews.common.view.stories

import androidx.compose.runtime.compositionLocalOf

expect value class PlatformContext(val platformContext: Any)

val LocalPlatformContext =
    compositionLocalOf<PlatformContext> { error("CompositionLocal LocalPlatformContext not present") }

expect fun PlatformContext.displayMessage(message: String)
