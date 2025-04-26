package com.dudoji.android.network.api.service

import retrofit2.Response
import retrofit2.http.GET

// user API endpoints
interface UserApiService {
    @GET("/api/user/info/get/profile_image")
    suspend fun getUserProfileImageUrl(): Response<String>

    @GET("/api/user/info/get/name")
    suspend fun getUserName(): Response<String>

    @GET("/api/user/info/get/email")
    suspend fun getUserEmail(): Response<String>
}