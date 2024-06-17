package com.monoid.hackernews.common.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
val ActualStoriesNavType =  NavType.EnumType(Story::class.java)

inline val NavType.Companion.StoryType: NavType<Story> get() = ActualStoriesNavType

data object ActualActionNavType : NavType<LoginAction>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): LoginAction? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): LoginAction {
        return jsonDecoder.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: LoginAction) {
        bundle.putString(key, Json.encodeToString(value))
    }

    fun encode(action: LoginAction): String {
        return Uri.encode(jsonDecoder.encodeToString(action))
    }
}

inline val NavType.Companion.ActionNavType: NavType<LoginAction> get() = ActualActionNavType

data object ActualItemIdNavType : NavType<ItemId>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: ItemId) {
        bundle.putLong(key, value.long)
    }

    override fun get(bundle: Bundle, key: String): ItemId {
        return ItemId(bundle.getLong(key))
    }

    override fun parseValue(value: String): ItemId {
        return ItemId(Uri.decode(value).toLong())
    }

    fun encodeValue(itemId: ItemId): String {
        return Uri.encode(itemId.long.toString())
    }
}

inline val NavType.Companion.ItemIdNavType: NavType<ItemId> get() = ActualItemIdNavType

data object ActualUsernameNavType : NavType<Username>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Username) {
        bundle.putString(key, value.string)
    }

    override fun get(bundle: Bundle, key: String): Username {
        return Username(bundle.getString(key)!!)
    }

    override fun parseValue(value: String): Username {
        return Username(Uri.decode(value))
    }

    fun encodeValue(username: Username): String {
        return Uri.encode(username.string)
    }
}

inline val NavType.Companion.UsernameNavType: NavType<Username> get() = ActualUsernameNavType
