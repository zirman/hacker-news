package com.monoid.hackernews.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    @SerialName("username")
    val username: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("font")
    val font: String = "",
)
