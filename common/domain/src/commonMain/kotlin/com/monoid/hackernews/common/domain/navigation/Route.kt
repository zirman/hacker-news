package com.monoid.hackernews.common.domain.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

expect fun encodeUrl(str: String): String
expect fun decodeUrl(str: String): String

//enum class StoryType {
//    Top,
//    New,
//    Best,
//    Ask,
//    Show,
//    Job,
//    Favorite,
//}

@Stable
@Serializable
sealed interface BottomNav : NavKey {
    @Serializable
    @SerialName("Stories")
    data object Stories : BottomNav

    @Serializable
    @SerialName("Favorites")
    data object Favorites : BottomNav

    @Serializable
    @SerialName("Settings")
    data object Settings : BottomNav
}

@Serializable
sealed interface Route : NavKey {
    @Serializable
    @SerialName("User")
    data class User(val username: Username) : Route

    @Serializable
    @SerialName("Story")
    data class Story(val itemId: ItemId) : Route

    @Serializable
    @SerialName("Reply")
    data class Reply(val parentId: ItemId) : Route

    @Serializable
    sealed interface Settings : Route {
        @Serializable
        @SerialName("Appearance")
        data object Appearance : Settings

        @Serializable
        @SerialName("Notifications")
        data object Notifications : Settings

        @Serializable
        @SerialName("Help")
        data object Help : Settings

        @Serializable
        @SerialName("TermsOfService")
        data object TermsOfService : Settings

        @Serializable
        @SerialName("UserGuidelines")
        data object UserGuidelines : Settings

        @Serializable
        @SerialName("SendFeedback")
        data object SendFeedback : Settings

        @Serializable
        @SerialName("About")
        data object About : Settings
    }
}
