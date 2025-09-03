package com.dudoji.android.data.remote

import com.dudoji.android.domain.model.User
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowApiService {

    @GET("/api/user/follows")
    suspend fun getUsers(
        @Query("type") type: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
        @Query("keyword") keyword: String
    ): Response<List<User>>

    @GET("/api/user/follows")
    suspend fun getFollowings(): Response<List<User>>

    @GET("/api/user/follows/follower")
    suspend fun getFollowers(): Response<List<User>?>

    @POST("/api/user/follows/{userId}")
    suspend fun addFriend(@Path("userId") userId: Long): Response<Boolean>

    @DELETE("/api/user/follows/{userId}")
    suspend fun deleteFriend(@Path("userId") userId: Long): Response<Boolean>

    @GET("/api/user/follows/recommended")
    suspend fun getRecommendedUsers(@Query("email") email: String): Response<List<User>>
}