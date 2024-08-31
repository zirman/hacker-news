package com.monoid.hackernews.common.data.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@SerialName("ItemId")
@JvmInline
actual value class ItemId actual constructor(
    actual val long: Long,
) : Parcelable
