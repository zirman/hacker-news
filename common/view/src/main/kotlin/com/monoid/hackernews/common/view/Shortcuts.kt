package com.monoid.hackernews.common.view

import android.content.Context
import androidx.core.content.pm.ShortcutManagerCompat
import com.monoid.hackernews.common.navigation.Stories

fun <T : Context> Context.updateAndPushDynamicShortcuts(contextClass: Class<T>) {
    val shortcuts = ShortcutManagerCompat
        .getShortcuts(this, ShortcutManagerCompat.FLAG_MATCH_DYNAMIC)

    val stories = Stories.entries

    val (update, push) = stories
        .partition { story -> shortcuts.any { it.id == story.name } }

    // update existing dynamic shortcuts
    ShortcutManagerCompat.updateShortcuts(
        this,
        update.map { it.toShortcutInfoCompat(this, contextClass) }
    )

    // add dynamic shortcuts that don't exist
    push.map { it.toShortcutInfoCompat(this, contextClass) }
        .forEach { ShortcutManagerCompat.pushDynamicShortcut(this, it) }
}
