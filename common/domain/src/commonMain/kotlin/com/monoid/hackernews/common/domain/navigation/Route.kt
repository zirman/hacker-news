package com.monoid.hackernews.common.domain.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.LoginAction
import com.monoid.hackernews.common.data.model.Username
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

enum class BottomNav {
    Stories,
    Favorites,
    Settings,
}

enum class Story {
    Top,
    New,
    Best,
    Ask,
    Show,
    Job,
    Favorite,
}

object Route {
    @Serializable
    @SerialName("Home")
    data object Home

    @Serializable
    @SerialName("Browser")
    data class Browser(val url: String)
}

val jsonDecoder: Json = Json { ignoreUnknownKeys = true }

data object ActualStoriesNavType : NavType<Story>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Story) {
        bundle.putString(key, value.name)
    }

    override fun get(bundle: Bundle, key: String): Story = Story.valueOf(bundle.getString(key)!!)
    override fun serializeAsValue(value: Story): String = value.name
    override fun parseValue(value: String): Story = Story.valueOf(value)
}

inline val NavType.Companion.StoryType: NavType<Story> get() = ActualStoriesNavType

data object ActualActionNavType : NavType<LoginAction>(isNullableAllowed = true) {
    override fun put(bundle: Bundle, key: String, value: LoginAction) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): LoginAction? =
        bundle.getString(key)?.let { Json.decodeFromString(it) }

    override fun serializeAsValue(value: LoginAction): String =
        URLEncoder.encode(jsonDecoder.encodeToString(value), "utf-8")

    override fun parseValue(value: String): LoginAction =
        jsonDecoder.decodeFromString(URLDecoder.decode(value, "utf-8"))
}

inline val NavType.Companion.ActionNavType: NavType<LoginAction> get() = ActualActionNavType

data object ActualItemIdNavType : NavType<ItemId>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: ItemId) {
        bundle.putLong(key, value.long)
    }

    override fun get(bundle: Bundle, key: String): ItemId = ItemId(bundle.getLong(key))
    override fun serializeAsValue(value: ItemId): String = value.long.toString()
    override fun parseValue(value: String): ItemId = ItemId(value.toLong())
}

inline val NavType.Companion.ItemIdNavType: NavType<ItemId> get() = ActualItemIdNavType

data object ActualUsernameNavType : NavType<Username>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Username) {
        bundle.putString(key, value.string)
    }

    override fun get(bundle: Bundle, key: String): Username = Username(bundle.getString(key)!!)
    override fun serializeAsValue(value: Username): String = URLEncoder.encode(value.string, "utf-8")
    override fun parseValue(value: String): Username = Username(URLDecoder.decode(value, "utf-8"))
}

inline val NavType.Companion.UsernameNavType: NavType<Username> get() = ActualUsernameNavType
