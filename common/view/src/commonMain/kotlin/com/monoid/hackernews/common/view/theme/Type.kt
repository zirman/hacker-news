package com.monoid.hackernews.common.view.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.monoid.hackernews.common.data.model.FontSize
import com.monoid.hackernews.common.data.model.LineHeight
import com.monoid.hackernews.common.data.model.ParagraphIndent

@Composable
fun rememberAppTypography(
    fontFamily: FontFamily,
    fontSizeDelta: FontSize,
    lineHeightDelta: LineHeight,
    paragraphIndent: ParagraphIndent,
): Typography = remember(fontFamily, fontSizeDelta, lineHeightDelta, paragraphIndent) {
    fun getTextStyle(
        baseFontSize: Int,
        spaceBetween: Int,
        fontWeight: FontWeight,
        letterSpacing: TextUnit,
    ): TextStyle {
        val fontSize = (baseFontSize + fontSizeDelta.delta).coerceAtLeast(10)
        return TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize.sp,
            lineHeight = (fontSize + spaceBetween + lineHeightDelta.delta).coerceAtLeast(fontSize).sp,
            letterSpacing = letterSpacing,
        )
    }
    Typography(
        displayLarge = getTextStyle(
            baseFontSize = 57,
            spaceBetween = 7,
            fontWeight = FontWeight.W400,
            letterSpacing = (-0.25).sp,
        ),
        displayMedium = getTextStyle(
            baseFontSize = 45,
            spaceBetween = 7,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
        ),
        displaySmall = getTextStyle(
            baseFontSize = 36,
            spaceBetween = 8,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
        ),
        headlineLarge = getTextStyle(
            baseFontSize = 32,
            spaceBetween = 8,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
        ),
        headlineMedium = getTextStyle(
            baseFontSize = 28,
            spaceBetween = 8,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
        ),
        headlineSmall = getTextStyle(
            baseFontSize = 24,
            spaceBetween = 8,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
        ),
        titleLarge = getTextStyle(
            baseFontSize = 22,
            spaceBetween = 6,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
        ),
        titleMedium = getTextStyle(
            baseFontSize = 16,
            spaceBetween = 8,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp,
        ),
        titleSmall = getTextStyle(
            baseFontSize = 14,
            spaceBetween = 6,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp,
        ),
        labelLarge = getTextStyle(
            baseFontSize = 14,
            spaceBetween = 6,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp,
        ),
        bodyLarge = getTextStyle(
            baseFontSize = 16,
            spaceBetween = 8,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.5.sp,
        ),
        bodyMedium = getTextStyle(
            baseFontSize = 14,
            spaceBetween = 6,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.25.sp,
        ),
        bodySmall = getTextStyle(
            baseFontSize = 12,
            spaceBetween = 4,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.4.sp,
        ),
        labelMedium = getTextStyle(
            baseFontSize = 12,
            spaceBetween = 4,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = getTextStyle(
            baseFontSize = 11,
            spaceBetween = 5,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
        ),
    )
}
