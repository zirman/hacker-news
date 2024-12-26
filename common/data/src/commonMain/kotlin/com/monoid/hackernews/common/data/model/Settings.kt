package com.monoid.hackernews.common.data.model

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Settings")
data class Settings(
    @SerialName("username")
    val username: Username = Username(""),
    @SerialName("password")
    val password: Password = Password(""),
    @SerialName("day_night")
    val lightDarkMode: LightDarkMode = LightDarkMode.default,
    @SerialName("font")
    val font: HNFont = HNFont.default,
    @SerialName("font_size")
    val fontSize: FontSize = FontSize.default,
    @SerialName("line_height")
    val lineHeight: LineHeight = LineHeight.default,
    @SerialName("paragraph_indent")
    val paragraphIndent: ParagraphIndent = ParagraphIndent.default,
    @SerialName("shape")
    val shape: Shape = Shape.default,
    @SerialName("Colors")
    val colors: Colors = Colors.default,
)

val SETTINGS_KEY = byteArrayPreferencesKey("settings")
expect var androidx.datastore.preferences.core.MutablePreferences.settings: Settings?
expect val androidx.datastore.preferences.core.Preferences.settings: Settings?

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
data class FontSize(val delta: Int) {
    fun increaseSize(): FontSize {
        return copy(delta = delta + 1)
    }

    fun decreaseSize(): FontSize = copy(delta = delta - 1)

    companion object {
        val default = FontSize(delta = 0)
    }
}

@Serializable
@SerialName("LineSpacing")
data class LineHeight(val delta: Int) {
    fun increaseSize(): LineHeight = copy(delta = delta + 1)
    fun decreaseSize(): LineHeight = copy(delta = delta - 1)

    companion object {
        val default = LineHeight(delta = 0)
    }
}

@Serializable
@SerialName("ParagraphIndent")
data class ParagraphIndent(val em: Int) {
    fun increaseSize(): ParagraphIndent = copy(em = em + 1)
    fun decreaseSize(): ParagraphIndent = copy(em = (em - 1).coerceAtLeast(0))

    companion object {
        val default = ParagraphIndent(em = 1)
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
