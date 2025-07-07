package com.dudoji.android.network.api.service

import retrofit2.Response
import retrofit2.http.GET

// user API endpoints
interface UserApiService {
    @GET("/api/user/infos/profile-image")
    suspend fun getUserProfileImageUrl(): Response<String>

    @GET("/api/user/infos/name")
    suspend fun getUserName(): Response<String>

    @GET("/api/user/infos/email")
    suspend fun getUserEmail(): Response<String>

    @GET("/api/user/infos/pinned-count")
    suspend fun getUserPinnedCount(): Response<Int>

    @GET("/api/user/infos/follower-count")
    suspend fun getUserFollowerCount(): Response<Int>

    @GET("/api/user/infos/following-count")
    suspend fun getUserFollowingCount(): Response<Int>

}