package com.dudoji.android.network.api.service

import retrofit2.Response
import retrofit2.http.GET

// user API endpoints
interface UserApiService {
    @GET("/api/user/infos/profile-image")
    suspend fun getUserProfileImageUrl(): Response<String>

    @GET("/api/user/infos/name")
    suspend fun getUserName(): Response<String>

    @GET("/api/user/profiles/mine")
    suspend fun getUserProfile(): Response<UserProfileDto>

    @GET("/api/user/infos/email")
    suspend fun getUserEmail(): Response<String>
}