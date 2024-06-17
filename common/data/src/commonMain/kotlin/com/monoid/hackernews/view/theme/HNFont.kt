package com.monoid.hackernews.view.theme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("HNFont")
enum class HNFont {
    @SerialName("SansSerif")
    SansSerif,

    @SerialName("Serif")
    Serif,

    @SerialName("Monospace")
    Monospace,

    @SerialName("Cursive")
    Cursive,
    ;

    companion object {
        val default = SansSerif
    }
}
