package com.monoid.hackernews.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.monoid.hackernews.common.view.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@Serializable
@SerialName("HNFont")
sealed class HNFont {
    @Composable
    abstract fun rememberFontFamily(): FontFamily

    @Composable
    abstract fun getName(): String

    @Serializable
    @SerialName("Default")
    data object Default : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily {
            return FontFamily.Default
        }

        @Composable
        override fun getName(): String {
            return stringResource(id = R.string.default_string)
        }
    }

    @Serializable
    @SerialName("SansSerif")
    data object SansSerif : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily {
            return FontFamily.SansSerif
        }

        @Composable
        override fun getName(): String {
            return stringResource(id = R.string.sans_serif)
        }
    }

    @Serializable
    @SerialName("Serif")
    data object Serif : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily = FontFamily.Serif

        @Composable
        override fun getName(): String {
            return stringResource(id = R.string.serif)
        }
    }

    @Serializable
    @SerialName("Monospace")
    data object Monospace : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily {
            return FontFamily.Monospace
        }

        @Composable
        override fun getName(): String {
            return stringResource(id = R.string.monospace)
        }
    }

    @Serializable
    @SerialName("Cursive")
    data object Cursive : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily {
            return FontFamily.Cursive
        }

        @Composable
        override fun getName(): String {
            return stringResource(id = R.string.cursive)
        }
    }

    @Serializable
    @SerialName("Google")
    data class Google(val googleFontName: String) : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily {
            return remember {
                FontFamily(Font(googleFont = GoogleFont(googleFontName), fontProvider = provider))
            }
        }

        @Composable
        override fun getName(): String {
            return googleFontName
        }
    }
}
