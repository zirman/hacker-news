package com.monoid.hackernews.common.data.api

import androidx.compose.runtime.Stable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.GeneratedSerializer
import kotlin.js.JsExport
import kotlin.jvm.JvmInline

@Stable
@Serializable
@SerialName("ItemId")
@JvmInline
actual value class ItemId actual constructor(actual val long: Long)
