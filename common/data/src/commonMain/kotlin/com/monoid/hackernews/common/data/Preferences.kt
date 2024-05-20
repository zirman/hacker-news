package com.monoid.hackernews.common.data

import com.monoid.hackernews.view.theme.HNFont
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Preferences")
data class Preferences(
    @SerialName("username")
    val username: Username = Username(""),
    @SerialName("password")
    val password: Password = Password(""),
    @SerialName("font")
    val font: HNFont = HNFont.Serif,
)
