package com.monoid.hackernews.common.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Username")
data class Username(
    @SerialName("string")
    val string: String,
)
