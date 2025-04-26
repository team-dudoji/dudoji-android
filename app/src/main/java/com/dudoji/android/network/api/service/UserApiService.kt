package com.dudoji.android.network.api.service


import com.dudoji.android.network.entity.TokenResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

// User Data API endpoints
interface UserApiService {
    @POST("auth/login/kakao/app-login")
    suspend fun kakaoLogin(@Header("Authorization") token: String): Response<TokenResponse>
}