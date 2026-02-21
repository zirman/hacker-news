package com.monoid.hackernews.common.data.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
@SerialName("Username")
data class Username(
    @SerialName("string")
    val string: String,
)
