package com.dudoji.android.network.api.service

import com.dudoji.android.mypage.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.GET

// user API endpoints
interface UserApiService {
    @GET("/api/user/profiles/mine")
    suspend fun getUserProfile(): Response<UserProfileDto>
}