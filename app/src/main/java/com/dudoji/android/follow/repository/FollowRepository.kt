package com.dudoji.android.follow.repository

import android.R
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.follow.domain.User

object FollowRepository {
    private val followings = mutableListOf<User>()

    private var isLoaded = false

     @RequiresApi(Build.VERSION_CODES.O)
     suspend fun getFollowings(): List<User> {
        if (!isLoaded) {
            loadFollowings()
            isLoaded = true
        }
        return followings
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addFollowing(user: User): Boolean {
        followings.add(user)
        return RetrofitClient.followApiService.addFriend(user.id).isSuccessful
    }


    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteFollowing(user: User): Boolean {
        val isSuccessful = RetrofitClient.followApiService.deleteFriend(user.id).isSuccessful
        if (isSuccessful) {
            followings.remove(user)
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
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