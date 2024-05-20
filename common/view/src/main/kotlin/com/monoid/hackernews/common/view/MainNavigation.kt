package com.monoid.hackernews.common.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import com.monoid.hackernews.common.navigation.Story

@StringRes
fun Story.toShortcutShortLabelStringId(): Int {
    return when (this) {
        Story.Top -> R.string.top_stories_shortcut_short_label
        Story.New -> R.string.new_stories_shortcut_short_label
        Story.Best -> R.string.best_stories_shortcut_short_label
        Story.Ask -> R.string.ask_hacker_news_shortcut_short_label
        Story.Show -> R.string.show_hacker_news_shortcut_short_label
        Story.Job -> R.string.jobs_shortcut_short_label
        Story.Favorite -> R.string.favorites_shortcut_short_label
    }
}

@StringRes
fun Story.toShortcutLongLabelStringId(): Int {
    return when (this) {
        Story.Top -> R.string.top_stories_shortcut_long_label
        Story.New -> R.string.new_stories_shortcut_long_label
        Story.Best -> R.string.best_stories_shortcut_long_label
        Story.Ask -> R.string.ask_hacker_news_shortcut_long_label
        Story.Show -> R.string.show_hacker_news_shortcut_long_label
        Story.Job -> R.string.jobs_shortcut_long_label
        Story.Favorite -> R.string.favorites_shortcut_long_label
    }
}

@DrawableRes
fun Story.toShortcutIconDrawableId(): Int {
    return when (this) {
        Story.Top -> R.drawable.trending_up_48px
        Story.New -> R.drawable.new_releases_48px
        Story.Best -> R.drawable.grade_48px
        Story.Ask -> R.drawable.forum_48px
        Story.Show -> R.drawable.present_to_all_48px
        Story.Job -> R.drawable.work_48px
        Story.Favorite -> R.drawable.bookmarks_48px
    }
}

fun <T : Context> Story.toShortcutInfoCompat(context: Context, contextClass: Class<T>): ShortcutInfoCompat {
    return ShortcutInfoCompat.Builder(context, name)
        .setShortLabel(context.resources.getString(toShortcutShortLabelStringId()))
        .setLongLabel(context.resources.getString(toShortcutLongLabelStringId()))
        .setIcon(IconCompat.createWithResource(context, toShortcutIconDrawableId()))
        .setIntent(
            Intent(context, contextClass).apply {
                action = Intent.ACTION_VIEW

                data = Uri.parse("https://news.ycombinator.com")
                    .buildUpon()
                    .appendPath(
                        when (this@toShortcutInfoCompat) {
                            Story.Top -> "news"
                            Story.New -> "newest"
                            Story.Best -> "best"
                            Story.Ask -> "ask"
                            Story.Show -> "show"
                            Story.Job -> "jobs"
                            Story.Favorite -> "favorites"
                        }
                    )
                    .build()
            }
        )
        .build()
}
