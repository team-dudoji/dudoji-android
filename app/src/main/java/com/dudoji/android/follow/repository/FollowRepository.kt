package com.dudoji.android.follow.repository

import android.util.Log
import com.dudoji.android.follow.domain.User

object FollowRepository {
    private val followings = mutableListOf<User>()

    private var isLoaded = false

     suspend fun getFollowings(): List<User> {
        if (!isLoaded) {
            loadFollowings()
            isLoaded = true
        }
        return followings
    }

    suspend fun addFollowing(user: User): Boolean {
        followings.add(user)
        return RetrofitClient.followApiService.addFriend(user.id).isSuccessful
    }

    suspend fun getRecommendedUsers(email: String): List<User> {
        val response = RetrofitClient.followApiService.getRecommendedUsers(email)
        if (response.isSuccessful) {
            return response.body() ?: emptyList();
        }
        return emptyList()
    }

    fun clearFollowings() {
        followings.clear()
    }

    suspend fun loadFollowings() {
        val response = RetrofitClient.followApiService.getFriends()
        if (response.isSuccessful) {
            response.body()?.let { users ->
                followings.clear()
                followings.addAll(users)
            }
            Log.d("FollowRepository", "Followings loaded successfully: $followings")
        } else {
            Log.e("FollowRepository", "Failed to load followings: ${response.errorBody()?.string()}")
        }
    }
}