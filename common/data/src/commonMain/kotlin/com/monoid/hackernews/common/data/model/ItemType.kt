package com.monoid.hackernews.common.data.model

import androidx.room3.ColumnTypeConverter

enum class ItemType {
    Story,
    Comment,
    Job,
    Poll,
    PollOpt,
    ;

    override fun toString(): String = name.lowercase()

    internal class Converter {

        @ColumnTypeConverter
        fun toItemType(value: String): ItemType = enumValueOf(value)

        @ColumnTypeConverter
        fun fromItemType(value: ItemType): String = value.name
    }
}
