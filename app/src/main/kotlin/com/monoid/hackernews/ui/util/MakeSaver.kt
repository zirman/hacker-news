package com.monoid.hackernews.ui.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.jsonDecoder
import com.monoid.hackernews.room.ItemTree
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

inline fun <reified T> makeSaver(): Saver<T, String> =
    object : Saver<T, String> {
        override fun restore(value: String): T =
            jsonDecoder.decodeFromString(value)

        override fun SaverScope.save(value: T): String =
            jsonDecoder.encodeToString(value)
    }

val itemIdSaver = makeSaver<ItemId?>()
val itemTreeSaver = makeSaver<ItemTree?>()
