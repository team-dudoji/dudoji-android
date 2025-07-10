package com.dudoji.android.network.api.service

import com.dudoji.android.follow.domain.User
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowApiService {

    @GET("/api/user/follows")
    suspend fun getFollowings(): Response<List<User>>

    @GET("/api/user/follows/follwer")
    suspend fun getFollowers(): Response<List<User>>

    @POST("/api/user/follows/{userId}")
    suspend fun addFriend(@Path("userId") userId: Long): Response<Boolean>

    @DELETE("/api/user/follows/{userId}")
    suspend fun deleteFriend(@Path("userId") userId: Long): Response<Boolean>

    @GET("/api/user/follows/recommended")
    suspend fun getRecommendedUsers(@Query("email") email: String): Response<List<User>>
}