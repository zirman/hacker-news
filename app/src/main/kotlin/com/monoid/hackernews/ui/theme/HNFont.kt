package com.monoid.hackernews.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.monoid.hackernews.R
import kotlinx.serialization.Serializable

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@Serializable
sealed class HNFont {
    @Composable
    abstract fun rememberFontFamily(): FontFamily

    @Composable
    abstract fun getName(): String

    @Serializable
    object Default : HNFont() {
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
    object SansSerif : HNFont() {
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
    object Serif : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily = FontFamily.Serif

        @Composable
        override fun getName(): String {
            return stringResource(id = R.string.serif)
        }
    }

    @Serializable
    object Monospace : HNFont() {
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
    object Cursive : HNFont() {
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
    data class Google(val googleFontName: String) : HNFont() {
        @Composable
        override fun rememberFontFamily(): FontFamily  {
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
