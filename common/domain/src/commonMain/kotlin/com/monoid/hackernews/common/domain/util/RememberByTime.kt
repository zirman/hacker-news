package com.monoid.hackernews.common.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import com.monoid.hackernews.common.data.html.DEFAULT_TEXT_LINK_STYLES
import com.monoid.hackernews.common.data.model.Username
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

const val USER_TAG = "USER"

@Composable
fun timeBy(
    time: Long?,
    by: Username?,
    onClick: (Username) -> Unit,
): AnnotatedString {
    return buildAnnotatedString {
        val dateTimePeriod = time
            ?.let { Instant.fromEpochSeconds(it) }
            ?.periodUntil(Clock.System.now(), TimeZone.UTC)
        pushLink(
            LinkAnnotation.Clickable(
                tag = USER_TAG,
                styles = DEFAULT_TEXT_LINK_STYLES,
            ) { by?.let { onClick(it) } },
        )
        if (by?.string != null) {
            append(by.string)
        }
        pop()
        // TODO: locale
        append(
            when {
                dateTimePeriod == null -> ""
                dateTimePeriod.years > 0 -> " - ${dateTimePeriod.years} years ago"
                dateTimePeriod.months > 0 -> " - ${dateTimePeriod.months} months ago"
                dateTimePeriod.days > 0 -> " - ${dateTimePeriod.days} days ago"
                dateTimePeriod.hours > 0 -> " - ${dateTimePeriod.hours} hours ago"
                else -> " - ${dateTimePeriod.minutes} minutes ago"
            },
        )
    }
}

@Composable
fun timeBy2(
    time: Long?,
    by: Username?,
): AnnotatedString {
    return buildAnnotatedString {
        val dateTimePeriod = time
            ?.let { Instant.fromEpochSeconds(it) }
            ?.periodUntil(Clock.System.now(), TimeZone.UTC)
        if (by?.string != null) {
            append(by.string)
        }
        // TODO: locale
        append(
            when {
                dateTimePeriod == null -> ""
                dateTimePeriod.years > 0 -> " - ${dateTimePeriod.years} years ago"
                dateTimePeriod.months > 0 -> " - ${dateTimePeriod.months} months ago"
                dateTimePeriod.days > 0 -> " - ${dateTimePeriod.days} days ago"
                dateTimePeriod.hours > 0 -> " - ${dateTimePeriod.hours} hours ago"
                else -> " - ${dateTimePeriod.minutes} minutes ago"
            },
        )
    }
}
