package com.monoid.hackernews.common.data.api

expect suspend fun getHtmlItems(path: String): List<ItemId>
