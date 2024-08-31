package com.monoid.hackernews.common.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Password")
@JvmInline
value class Password(
    @SerialName("string")
    val string: String,
)
