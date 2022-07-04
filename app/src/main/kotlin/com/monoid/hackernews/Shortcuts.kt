package com.monoid.hackernews

import android.content.Context
import androidx.core.content.pm.ShortcutManagerCompat

fun Context.updateAndPushDynamicShortcuts() {
    val shortcuts = ShortcutManagerCompat
        .getShortcuts(this, ShortcutManagerCompat.FLAG_MATCH_DYNAMIC)

    val stories = Stories.values()

    val (update, push) = stories
        .partition { story -> shortcuts.any { it.id == story.name } }

    // update existing dynamic shortcuts
    ShortcutManagerCompat.updateShortcuts(
        this,
        update.map { it.toShortcutInfoCompat(this) }
    )

    // add dynamic shortcuts that don't exist
    push.map { it.toShortcutInfoCompat(this) }
        .forEach { ShortcutManagerCompat.pushDynamicShortcut(this, it) }
}
