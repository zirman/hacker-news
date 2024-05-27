package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId

interface SimpleItemUiState {
    val id: ItemId
    val lastUpdate: Long?
    val type: String?
    val time: Long?
    val deleted: Boolean?
    val by: String?
    val descendants: Int?
    val score: Int?
    val title: String?
    val text: String?
    val url: String?
    val parent: ItemId?
    val kids: List<ItemId>?
    val isUpvote: Boolean?
    val isFavorite: Boolean?
    val isFlag: Boolean?
    val isExpanded: Boolean?
    val isFollowed: Boolean?
}

expect fun makeSimpleItemUiState(
    id: ItemId,
    lastUpdate: Long? = null,
    type: String? = null,
    time: Long? = null,
    deleted: Boolean? = null,
    by: String? = null,
    descendants: Int? = null,
    score: Int? = null,
    title: String? = null,
    text: String? = null,
    url: String? = null,
    parent: ItemId? = null,
    kids: List<ItemId>? = null,
    isUpvote: Boolean? = null,
    isFavorite: Boolean? = null,
    isFlag: Boolean? = null,
    isExpanded: Boolean? = null,
    isFollowed: Boolean? = null,
): SimpleItemUiState
