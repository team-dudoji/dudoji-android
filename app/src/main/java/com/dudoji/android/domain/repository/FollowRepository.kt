package com.dudoji.android.domain.repository

import com.dudoji.android.domain.model.User

interface FollowRepository {

    suspend fun followUser(userId: Long)
    suspend fun unfollowUser(userId: Long)
    suspend fun getUsers(
        userType: UserType = UserType.FOLLOWING,
        page: Int = 0,
        size: Int = 20,
        sortType: SortType = SortType.NEWEST,
        keyword: String = ""
    ): List<User>

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

    enum class UserType {
        FOLLOWER,
        FOLLOWING,
        NONE
    }
}