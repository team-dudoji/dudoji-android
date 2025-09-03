package com.dudoji.android.domain.model

enum class SortType(val value: String) {
        NEWEST("createdAt,desc"),
        OLDEST("createdAt,asc"),
        FOLLOWED_AT_NEWEST("followedAt,desc"),
        FOLLOWED_AT_OLDEST("followedAt,asc"),
        FOLLOWING_AT_NEWEST("followingAt,desc"),
        FOLLOWING_AT_OLDEST("followingAt,asc"),
        NAME_ASC("name,asc"),
        NAME_DESC("name,desc")
    }