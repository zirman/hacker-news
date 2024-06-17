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
    @SerialName("day_night")
    val lightDarkMode: LightDarkMode = LightDarkMode.default,
    @SerialName("font")
    val font: HNFont = HNFont.default,
    @SerialName("shape")
    val shape: Shape = Shape.default,
    @SerialName("Colors")
    val colors: Colors = Colors.default,
)

@Serializable
@SerialName("DayNight")
enum class LightDarkMode {
    @SerialName("system")
    System,

    @SerialName("light")
    Light,

    @SerialName("dark")
    Dark,
    ;

    companion object {
        val default = System
    }
}

@Serializable
@SerialName("FontSize")
enum class FontSize {
    @SerialName("system")
    System,

    @SerialName("small")
    Small,

    @SerialName("large")
    Large,
    ;

    companion object {
        val default = System
    }
}


@Serializable
@SerialName("Shape")
enum class Shape {
    @SerialName("rounded")
    Rounded,

    @SerialName("cut")
    Cut,
    ;

    companion object {
        val default = Cut
    }
}

@Serializable
@SerialName("Colors")
enum class Colors {
    @SerialName("material")
    Material,

    @SerialName("custom")
    Custom,
    ;

    companion object {
        val default = Material
    }
}
