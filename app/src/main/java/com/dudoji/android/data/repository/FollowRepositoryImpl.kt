package com.dudoji.android.data.repository

import RetrofitClient
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.data.remote.FollowApiService
import com.dudoji.android.domain.model.User
import com.dudoji.android.domain.repository.FollowRepository
import com.dudoji.android.domain.repository.FollowRepository.SortType
import com.dudoji.android.domain.repository.FollowRepository.UserType
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class FollowRepositoryImpl @Inject constructor(
    val followApiService: FollowApiService
): FollowRepository {
    override suspend fun getFollowersNum(): Int {
        return followApiService.getFollowers().body()?.size ?: 0
    }

    override suspend fun getFollowingsNum(): Int {
        return followApiService.getFollowings().body()?.size ?: 0
    }

    override suspend fun followUser(userId: Long) {
        RetrofitClient.followApiService.addFriend(userId)
    }

    override suspend fun unfollowUser(userId: Long) {
        followApiService.deleteFriend(userId)
    }

    override suspend fun getUsers(
        userType: UserType,
        page: Int,
        size: Int,
        sortType: SortType,
        keyword: String
    ): List<User> {
         return when (userType) {
             FollowRepository.UserType.FOLLOWER -> followApiService.getFollowers().body().orEmpty()
             FollowRepository.UserType.FOLLOWING -> followApiService.getFollowings().body().orEmpty()
             FollowRepository.UserType.NONE -> followApiService.getRecommendedUsers(keyword).body().orEmpty()
         }
    }
}