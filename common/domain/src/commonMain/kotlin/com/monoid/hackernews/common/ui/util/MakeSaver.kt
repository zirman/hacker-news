package com.monoid.hackernews.common.ui.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.navigation.jsonDecoder
import kotlinx.serialization.encodeToString

inline fun <reified T> makeSaver(): Saver<T, String> =
    object : Saver<T, String> {
        override fun restore(value: String): T =
            jsonDecoder.decodeFromString(value)

        override fun SaverScope.save(value: T): String =
            jsonDecoder.encodeToString(value)
    }

val itemIdSaver = makeSaver<ItemId?>()
