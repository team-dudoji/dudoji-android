package com.dudoji.android.network.api.service


import com.dudoji.android.network.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

// login API endpoints
interface LoginApiService {
    @POST("auth/login/kakao/app-login")
    suspend fun kakaoLogin(@Header("Authorization") token: String): Response<TokenResponse>
}