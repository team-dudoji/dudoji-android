package com.dudoji.android.mypage.api.service

import com.dudoji.android.mypage.api.dto.UserProfileDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// user API endpoints
interface UserApiService {

    @GET("/api/user/profiles/mine")
    suspend fun getUserProfile(): Response<UserProfileDto>

    @GET("/api/user/profiles/mine/profile-image")
    suspend fun getUserProfileImageUrl(): Response<String>

    @GET("/api/user/profiles/mine/name")
    suspend fun getUserName(): Response<String>

    @GET("/api/user/profiles/mine/email")
    suspend fun getUserEmail(): Response<String>

    @GET("/api/user/profiles/mine/coin")
    fun getUserCoin(): Call<Int>

    @GET("/api/user/profiles/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<UserProfileDto>
}