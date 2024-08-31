package com.monoid.hackernews.common.data.model

import androidx.room.TypeConverter

enum class ItemType {
    Story,
    Comment,
    Job,
    Poll,
    PollOpt,
    ;

    override fun toString(): String = name.lowercase()

    internal class Converter {

        @TypeConverter
        fun toItemType(value: String): ItemType = enumValueOf(value)

        @TypeConverter
        fun fromItemType(value: ItemType): String = value.name
    }
}
