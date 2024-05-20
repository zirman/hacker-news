package com.monoid.hackernews.view.theme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("HNFont")
sealed interface HNFont {

    @Serializable
    @SerialName("SansSerif")
    data object SansSerif : HNFont

    @Serializable
    @SerialName("Serif")
    data object Serif : HNFont

    @Serializable
    @SerialName("Monospace")
    data object Monospace : HNFont

    @Serializable
    @SerialName("Cursive")
    data object Cursive : HNFont
}
