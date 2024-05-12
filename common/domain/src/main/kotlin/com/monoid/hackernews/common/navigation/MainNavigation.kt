package com.monoid.hackernews.common.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Parcelize
enum class Stories : Parcelable {
    Top,
    New,
    Best,
    Ask,
    Show,
    Job,
    Favorite,
}

val jsonDecoder: Json = Json { ignoreUnknownKeys = true }

data object StoriesNavType : NavType<Stories>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Stories) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): Stories? {
        return if (Build.VERSION.SDK_INT >= 33) {
            bundle.getParcelable(key, Stories::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): Stories {
        return Stories.valueOf(Uri.decode(value))
    }

    fun encodeValue(stories: Stories): String {
        return Uri.encode(stories.name)
    }
}

private data object ActionNavType : NavType<LoginAction>(isNullableAllowed = true) {
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

data object ItemIdNavType : NavType<ItemId>(isNullableAllowed = false) {
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

data object UsernameNavType : NavType<Username>(isNullableAllowed = false) {
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

sealed class MainNavigation<T : Any> {
    companion object {
        private const val STORIES_KEY = "stories"
        private const val USERNAME_KEY = "username"
        private const val ACTION_KEY = "action"
        private const val ITEM_ID_KEY = "itemId"

        fun fromRoute(route: String?): MainNavigation<*>? {
            return when (route) {
                Home.route ->
                    Home

                Login.route ->
                    Login

                Reply.route ->
                    Reply

                User.route ->
                    User

                else ->
                    null
            }
        }
    }

    abstract val route: String
    abstract val arguments: List<NamedNavArgument>
    abstract fun routeWithArgs(args: T): String
    abstract fun argsFromRoute(navBackStackEntry: NavBackStackEntry): T?

    @Suppress("RedundantNullableReturnType")
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        { fadeIn() }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null

    data object Home : MainNavigation<Stories>() {
        override val route: String =
            "home?$STORIES_KEY={$STORIES_KEY}"

        override val arguments: List<NamedNavArgument>
            get() =
                listOf(
                    navArgument(STORIES_KEY) {
                        type = StoriesNavType
                        defaultValue = Stories.Top
                        nullable = false
                    }
                )

        override fun routeWithArgs(args: Stories): String =
            "home?$STORIES_KEY=${StoriesNavType.encodeValue(args)}"

        override fun argsFromRoute(navBackStackEntry: NavBackStackEntry): Stories {
            val arguments = navBackStackEntry.arguments!!

            return when (
                if (Build.VERSION.SDK_INT >= 33) {
                    arguments.getParcelable(NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    arguments.getParcelable(NavController.KEY_DEEP_LINK_INTENT)
                }?.data?.pathSegments
                    ?.firstOrNull()
                    ?.lowercase()
            ) {
                "news" ->
                    Stories.Top

                "newest" ->
                    Stories.New

                "best" ->
                    Stories.Best

                "show" ->
                    Stories.Show

                "ask" ->
                    Stories.Ask

                "jobs" ->
                    Stories.Job

                "favorites" ->
                    Stories.Favorite

                else ->
                    if (Build.VERSION.SDK_INT >= 33) {
                        navBackStackEntry.arguments?.getParcelable(STORIES_KEY, Stories::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        navBackStackEntry.arguments?.getParcelable(STORIES_KEY)
                    } ?: Stories.Top
            }
        }
    }

    data object Login : MainNavigation<LoginAction>() {
        override val route: String get() = "login?$ACTION_KEY={$ACTION_KEY}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ACTION_KEY) {
                    type = ActionNavType
                    defaultValue = LoginAction.Login
                }
            )

        override fun routeWithArgs(args: LoginAction): String =
            "login?$ACTION_KEY=${ActionNavType.encode(args)}"

        override fun argsFromRoute(navBackStackEntry: NavBackStackEntry): LoginAction {
            return ActionNavType[navBackStackEntry.arguments!!, ACTION_KEY]!!
        }
    }

    data object Reply : MainNavigation<ItemId>() {
        override val route: String get() = "reply?$ITEM_ID_KEY={$ITEM_ID_KEY}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(ITEM_ID_KEY) {
                    type = ItemIdNavType
                }
            )

        override fun routeWithArgs(args: ItemId): String =
            "reply?$ITEM_ID_KEY=${ItemIdNavType.encodeValue(args)}"

        override fun argsFromRoute(navBackStackEntry: NavBackStackEntry): ItemId {
            return ItemIdNavType[navBackStackEntry.arguments!!, ITEM_ID_KEY]
        }
    }

    data object User : MainNavigation<Username>() {
        override val route: String get() = "user?$USERNAME_KEY={$USERNAME_KEY}"

        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(USERNAME_KEY) {
                    type = UsernameNavType
                }
            )

        override fun routeWithArgs(args: Username): String =
            "user?$USERNAME_KEY=${UsernameNavType.encodeValue(args)}"

        override fun argsFromRoute(navBackStackEntry: NavBackStackEntry): Username {
            return UsernameNavType[navBackStackEntry.arguments!!, USERNAME_KEY]
        }
    }

    data object AboutUs : MainNavigation<Unit>() {
        override val route: String get() = "about-us"

        override val arguments: List<NamedNavArgument>
            get() = listOf()

        override fun routeWithArgs(args: Unit): String =
            "about-us"

        override fun argsFromRoute(navBackStackEntry: NavBackStackEntry) {
            return
        }
    }

    data object Settings : MainNavigation<Unit>() {
        override val route: String get() = "settings"

        override val arguments: List<NamedNavArgument>
            get() = listOf()

        override fun routeWithArgs(args: Unit): String =
            "settings"

        override fun argsFromRoute(navBackStackEntry: NavBackStackEntry) {
            return
        }
    }
}
