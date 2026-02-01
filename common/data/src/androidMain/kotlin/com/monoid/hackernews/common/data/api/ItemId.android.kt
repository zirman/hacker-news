package com.monoid.hackernews.common.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ItemId")
@JvmInline
actual value class ItemId actual constructor(actual val long: Long)
