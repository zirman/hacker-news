package com.monoid.hackernews.common.api

expect suspend fun getHtmlItems(path: String): List<ItemId>
