package com.monoid.hackernews.common.domain.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.LoginAction
import com.monoid.hackernews.common.data.model.Username
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

expect fun encodeUrl(str: String): String
expect fun decodeUrl(str: String): String

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
    sealed interface BottomNav {
        @Serializable
        @SerialName("Stories")
        data object Stories : BottomNav {
            override val route: String get() = "Stories"
        }

        @Serializable
        @SerialName("Favorites")
        data object Favorites : BottomNav {
            override val route: String get() = "Favorites"
        }

        @Serializable
        @SerialName("Settings")
        data object Settings : BottomNav {
            override val route: String get() = "Settings"
        }

        val route: String
        val ordinal: Int get() = entries.indexOf(this)

        companion object {
            // type needs to be specified or we get a compile error
            val entries: List<BottomNav> = listOf(Stories, Favorites, Settings)
        }
    }

    @Serializable
    @SerialName("Home")
    data object Home

    @Serializable
    @SerialName("User")
    data class User(val username: Username)

    @Serializable
    @SerialName("Story")
    data class Story(val itemId: ItemId)

    @Serializable
    @SerialName("Reply")
    data class Reply(val parentId: ItemId)

    object Settings {
        @Serializable
        @SerialName("Appearance")
        data object Appearance

        @Serializable
        @SerialName("Notifications")
        data object Notifications

        @Serializable
        @SerialName("Help")
        data object Help

        @Serializable
        @SerialName("TermsOfService")
        data object TermsOfService

        @Serializable
        @SerialName("UserGuidelines")
        data object UserGuidelines

        @Serializable
        @SerialName("SendFeedback")
        data object SendFeedback

        @Serializable
        @SerialName("About")
        data object About
    }
}

val jsonDecoder: Json = Json { ignoreUnknownKeys = true }

data object ActualStoriesNavType : NavType<Story>(isNullableAllowed = false) {
    override fun serializeAsValue(value: Story): String = value.name
    override fun get(bundle: SavedState, key: String) =
        Story.valueOf(bundle.read { getString(key) })

    override fun parseValue(value: String): Story = Story.valueOf(value)
    override fun put(bundle: SavedState, key: String, value: Story) {
        bundle.write { putString(key, value.name) }
    }
}

inline val NavType.Companion.StoryType: NavType<Story> get() = ActualStoriesNavType

data object ActualActionNavType : NavType<LoginAction>(isNullableAllowed = true) {
    override fun serializeAsValue(value: LoginAction): String =
        encodeUrl(jsonDecoder.encodeToString(value))

    override fun get(bundle: SavedState, key: String): LoginAction? =
        bundle.read { getString(key) }.let { Json.decodeFromString(it) }

    override fun parseValue(value: String): LoginAction =
        jsonDecoder.decodeFromString(decodeUrl(value))

    override fun put(bundle: SavedState, key: String, value: LoginAction) {
        bundle.write { putString(key, Json.encodeToString(value)) }
    }
}

inline val NavType.Companion.ActionNavType: NavType<LoginAction> get() = ActualActionNavType

data object ActualItemIdNavType : NavType<ItemId>(isNullableAllowed = false) {
    override fun serializeAsValue(value: ItemId): String = value.long.toString()
    override fun get(bundle: SavedState, key: String): ItemId =
        ItemId(bundle.read { getLong(key) })

    override fun parseValue(value: String): ItemId = ItemId(value.toLong())
    override fun put(bundle: SavedState, key: String, value: ItemId) {
        bundle.write { putLong(key, value.long) }
    }
}

inline val NavType.Companion.ItemIdNavType: NavType<ItemId> get() = ActualItemIdNavType

data object ActualUsernameNavType : NavType<Username>(isNullableAllowed = false) {
    override fun serializeAsValue(value: Username): String = encodeUrl(value.string)
    override fun get(bundle: SavedState, key: String): Username =
        Username(bundle.read { getString(key) })

    override fun parseValue(value: String): Username = Username(decodeUrl(value))
    override fun put(bundle: SavedState, key: String, value: Username) {
        bundle.write { putString(key, value.string) }
    }
}

inline val NavType.Companion.UsernameNavType: NavType<Username> get() = ActualUsernameNavType
