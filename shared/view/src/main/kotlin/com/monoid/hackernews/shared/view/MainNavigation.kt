package com.monoid.hackernews.shared.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import com.monoid.hackernews.shared.navigation.Stories

@StringRes
fun Stories.toShortcutShortLabelStringId(): Int {
    return when (this) {
        Stories.Top -> R.string.top_stories_shortcut_short_label
        Stories.New -> R.string.new_stories_shortcut_short_label
        Stories.Best -> R.string.best_stories_shortcut_short_label
        Stories.Ask -> R.string.ask_hacker_news_shortcut_short_label
        Stories.Show -> R.string.show_hacker_news_shortcut_short_label
        Stories.Job -> R.string.jobs_shortcut_short_label
        Stories.Favorite -> R.string.favorites_shortcut_short_label
    }
}

@StringRes
fun Stories.toShortcutLongLabelStringId(): Int {
    return when (this) {
        Stories.Top -> R.string.top_stories_shortcut_long_label
        Stories.New -> R.string.new_stories_shortcut_long_label
        Stories.Best -> R.string.best_stories_shortcut_long_label
        Stories.Ask -> R.string.ask_hacker_news_shortcut_long_label
        Stories.Show -> R.string.show_hacker_news_shortcut_long_label
        Stories.Job -> R.string.jobs_shortcut_long_label
        Stories.Favorite -> R.string.favorites_shortcut_long_label
    }
}

@DrawableRes
fun Stories.toShortcutIconDrawableId(): Int {
    return when (this) {
        Stories.Top -> R.drawable.trending_up_48px
        Stories.New -> R.drawable.new_releases_48px
        Stories.Best -> R.drawable.grade_48px
        Stories.Ask -> R.drawable.forum_48px
        Stories.Show -> R.drawable.present_to_all_48px
        Stories.Job -> R.drawable.work_48px
        Stories.Favorite -> R.drawable.bookmarks_48px
    }
}

fun <T : Context> Stories.toShortcutInfoCompat(context: Context, contextClass: Class<T>): ShortcutInfoCompat {
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
                            Stories.Top -> "news"
                            Stories.New -> "newest"
                            Stories.Best -> "best"
                            Stories.Ask -> "ask"
                            Stories.Show -> "show"
                            Stories.Job -> "jobs"
                            Stories.Favorite -> "favorites"
                        }
                    )
                    .build()
            }
        )
        .build()
}
