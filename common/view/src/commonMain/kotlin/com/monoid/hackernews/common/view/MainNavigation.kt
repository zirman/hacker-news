package com.monoid.hackernews.common.view

//import com.monoid.hackernews.common.domain.navigation.StoryType
//import org.jetbrains.compose.resources.DrawableResource
//import org.jetbrains.compose.resources.StringResource
//
//fun StoryType.toShortcutShortLabelStringId(): StringResource = when (this) {
//    StoryType.Top -> Res.string.top_stories_shortcut_short_label
//    StoryType.New -> Res.string.new_stories_shortcut_short_label
//    StoryType.Best -> Res.string.best_stories_shortcut_short_label
//    StoryType.Ask -> Res.string.ask_hacker_news_shortcut_short_label
//    StoryType.Show -> Res.string.show_hacker_news_shortcut_short_label
//    StoryType.Job -> Res.string.jobs_shortcut_short_label
//    StoryType.Favorite -> Res.string.favorites_shortcut_short_label
//}
//
//fun StoryType.toShortcutLongLabelStringId(): StringResource = when (this) {
//    StoryType.Top -> Res.string.top_stories_shortcut_long_label
//    StoryType.New -> Res.string.new_stories_shortcut_long_label
//    StoryType.Best -> Res.string.best_stories_shortcut_long_label
//    StoryType.Ask -> Res.string.ask_hacker_news_shortcut_long_label
//    StoryType.Show -> Res.string.show_hacker_news_shortcut_long_label
//    StoryType.Job -> Res.string.jobs_shortcut_long_label
//    StoryType.Favorite -> Res.string.favorites_shortcut_long_label
//}
//
//fun StoryType.toShortcutIconDrawableId(): DrawableResource = when (this) {
//    StoryType.Top -> Res.drawable.trending_up_48px
//    StoryType.New -> Res.drawable.new_releases_48px
//    StoryType.Best -> Res.drawable.grade_48px
//    StoryType.Ask -> Res.drawable.forum_48px
//    StoryType.Show -> Res.drawable.present_to_all_48px
//    StoryType.Job -> Res.drawable.work_48px
//    StoryType.Favorite -> Res.drawable.bookmarks_48px
//}

// fun <T : Context> StoryType.toShortcutInfoCompat(
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
//                        StoryType.Top -> "news"
//                        StoryType.New -> "newest"
//                        StoryType.Best -> "best"
//                        StoryType.Ask -> "ask"
//                        StoryType.Show -> "show"
//                        StoryType.Job -> "jobs"
//                        StoryType.Favorite -> "favorites"
//                    }
//                )
//                .build()
//        }
//    )
//    .build()
