package com.monoid.hackernews.common.data.api

import androidx.compose.runtime.Stable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
@SerialName("ItemId")
@JvmInline
actual value class ItemId actual constructor(actual val long: Long)
