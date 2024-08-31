@file:OptIn(ExperimentalTextApi::class)

package com.monoid.hackernews.common.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

const val USER_TAG = "USER"

@Composable
fun rememberTimeBy(time: Long?, by: String?): AnnotatedString =
    remember(time, by) {
        buildAnnotatedString {
            val userSpanStyle = TextStyle()
                .copy(textDecoration = TextDecoration.Underline)
                .toSpanStyle()

            val dateTimePeriod = time
                ?.let { Instant.fromEpochSeconds(it) }
                ?.periodUntil(Clock.System.now(), TimeZone.UTC)

            append(
                when {
                    dateTimePeriod == null -> ""
                    dateTimePeriod.years > 0 -> "${dateTimePeriod.years} years ago by "
                    dateTimePeriod.months > 0 -> "${dateTimePeriod.months} months ago by "
                    dateTimePeriod.days > 0 -> "${dateTimePeriod.days} days ago by "
                    dateTimePeriod.hours > 0 -> "${dateTimePeriod.hours} hours ago by "
                    else -> "${dateTimePeriod.minutes} minutes ago by "
                }
            )

            withAnnotation(
                tag = USER_TAG,
                annotation = by.orEmpty(),
            ) { withStyle(style = userSpanStyle) { append(by.orEmpty()) } }
        }
    }
