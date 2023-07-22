package com.monoid.hackernews.common.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LoginAction")
sealed class LoginAction : Parcelable {
    @Serializable
    @SerialName("Login")
    @Parcelize
    data object Login : LoginAction()

    @Serializable
    @SerialName("Upvote")
    @Parcelize
    data class Upvote(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction()

    @Serializable
    @SerialName("Favorite")
    @Parcelize
    data class Favorite(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction()

    @Serializable
    @SerialName("Flag")
    @Parcelize
    data class Flag(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction()

    @Serializable
    @SerialName("Reply")
    @Parcelize
    data class Reply(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction()
}
