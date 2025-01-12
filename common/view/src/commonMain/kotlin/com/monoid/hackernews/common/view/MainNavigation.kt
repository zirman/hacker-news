package com.monoid.hackernews.common.view

import com.monoid.hackernews.common.domain.navigation.Story
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

fun Story.toShortcutShortLabelStringId(): StringResource = when (this) {
    Story.Top -> Res.string.top_stories_shortcut_short_label
    Story.New -> Res.string.new_stories_shortcut_short_label
    Story.Best -> Res.string.best_stories_shortcut_short_label
    Story.Ask -> Res.string.ask_hacker_news_shortcut_short_label
    Story.Show -> Res.string.show_hacker_news_shortcut_short_label
    Story.Job -> Res.string.jobs_shortcut_short_label
    Story.Favorite -> Res.string.favorites_shortcut_short_label
}

fun Story.toShortcutLongLabelStringId(): StringResource = when (this) {
    Story.Top -> Res.string.top_stories_shortcut_long_label
    Story.New -> Res.string.new_stories_shortcut_long_label
    Story.Best -> Res.string.best_stories_shortcut_long_label
    Story.Ask -> Res.string.ask_hacker_news_shortcut_long_label
    Story.Show -> Res.string.show_hacker_news_shortcut_long_label
    Story.Job -> Res.string.jobs_shortcut_long_label
    Story.Favorite -> Res.string.favorites_shortcut_long_label
}

fun Story.toShortcutIconDrawableId(): DrawableResource = when (this) {
    Story.Top -> Res.drawable.trending_up_48px
    Story.New -> Res.drawable.new_releases_48px
    Story.Best -> Res.drawable.grade_48px
    Story.Ask -> Res.drawable.forum_48px
    Story.Show -> Res.drawable.present_to_all_48px
    Story.Job -> Res.drawable.work_48px
    Story.Favorite -> Res.drawable.bookmarks_48px
}

// fun <T : Context> Story.toShortcutInfoCompat(
//    context: Context,
//    contextClass: Class<T>,
// ): ShortcutInfoCompat = ShortcutInfoCompat
//    .Builder(context, name)
//    .setShortLabel(context.resources.getString(toShortcutShortLabelStringId()))
//    .setLongLabel(context.resources.getString(toShortcutLongLabelStringId()))
//    .setIcon(IconCompat.createWithResource(context, toShortcutIconDrawableId()))
//    .setIntent(
//        Intent(context, contextClass).apply {
//            action = Intent.ACTION_VIEW
//
//            data = Uri.parse("https://news.ycombinator.com")
//                .buildUpon()
//                .appendPath(
//                    when (this@toShortcutInfoCompat) {
//                        Story.Top -> "news"
//                        Story.New -> "newest"
//                        Story.Best -> "best"
//                        Story.Ask -> "ask"
//                        Story.Show -> "show"
//                        Story.Job -> "jobs"
//                        Story.Favorite -> "favorites"
//                    }
//                )
//                .build()
//        }
//    )
//    .build()
