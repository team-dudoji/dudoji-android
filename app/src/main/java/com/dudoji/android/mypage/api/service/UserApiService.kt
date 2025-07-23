package com.dudoji.android.mypage.api.service

import com.dudoji.android.mypage.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// user API endpoints
interface UserApiService {
    @GET("/api/user/infos/profile-image")
    suspend fun getUserProfileImageUrl(): Response<String>

    @GET("/api/user/infos/name")
    suspend fun getUserName(): Response<String>

    @GET("/api/user/profiles/mine")
    suspend fun getUserProfile(): Response<UserProfileDto>

    @GET("/api/user/profiles/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<UserProfileDto>

    @GET("/api/user/infos/email")
    suspend fun getUserEmail(): Response<String>
}