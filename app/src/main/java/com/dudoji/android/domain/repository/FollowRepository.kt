package com.dudoji.android.domain.repository

import com.dudoji.android.domain.model.SortType
import com.dudoji.android.domain.model.User
import com.dudoji.android.domain.model.UserType

interface FollowRepository {

    suspend fun getFollowersNum(): Int
    suspend fun getFollowingsNum(): Int
    suspend fun followUser(userId: Long)
    suspend fun unfollowUser(userId: Long)
    suspend fun getUsers(
        userType: UserType = UserType.FOLLOWING,
        page: Int = 0,
        size: Int = 20,
        sortType: SortType = SortType.NEWEST,
        keyword: String = ""
    ): List<User>
}