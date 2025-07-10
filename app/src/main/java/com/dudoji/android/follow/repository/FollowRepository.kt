package com.dudoji.android.follow.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.follow.domain.User

object FollowRepository {
    private val followings = mutableListOf<User>()
    private val followers = mutableListOf<User>()

    private var isFollowingsLoaded = false
    private var isFollowersLoaded = false

     @RequiresApi(Build.VERSION_CODES.O)
     suspend fun getFollowings(): List<User> {
        if (!isFollowingsLoaded) {
            loadFollowings()
            isFollowingsLoaded = true
        }
        return followings
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getFollowers(): List<User> {
        if (!isFollowersLoaded) {
            loadFollowers()
            isFollowersLoaded = true
        }
        return followers
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
        val response = RetrofitClient.followApiService.getFollowings()
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadFollowers() {
        val response = RetrofitClient.followApiService.getFollowers()
        if (response.isSuccessful) {
            response.body()?.let { users ->
                followers.clear()
                followers.addAll(users)
            }
            Log.d("FollowRepository", "Followers loaded successfully: $followers")
        } else {
            Log.e("FollowRepository", "Failed to load followers: ${response.errorBody()?.string()}")
        }
    }



}