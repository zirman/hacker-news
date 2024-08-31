package com.monoid.hackernews.common.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LoginAction")
sealed interface LoginAction {

    @Serializable
    @SerialName("Upvote")
    data class Upvote(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction

    @Serializable
    @SerialName("Favorite")
    data class Favorite(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction

    @Serializable
    @SerialName("Flag")
    data class Flag(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction

    @Serializable
    @SerialName("Reply")
    data class Reply(
        @SerialName("itemId")
        val itemId: Long,
    ) : LoginAction
}
