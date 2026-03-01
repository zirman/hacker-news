package com.monoid.hackernews.common.data.api

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
actual value class ItemId actual constructor(actual val long: Long)
