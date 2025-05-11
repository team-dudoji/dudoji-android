package com.dudoji.android.network.api.service

import com.dudoji.android.friend.domain.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendApiService {
    @GET("/api/user/friends")
    suspend fun getFriends(): Response<List<User>>

    @POST("/api/user/friends")
    suspend fun addFriend(friendId: Long): Response<Boolean>

    @GET("/api/user/friends/recommended")
    suspend fun getRecommendedFriends(@Query("email") email: String): Response<List<User>>
}