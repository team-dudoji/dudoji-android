package com.dudoji.android.login.api.service


import com.dudoji.android.login.api.dto.TokenResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// login API endpoints
interface LoginApiService {
    @POST("auth/login/kakao/app-login")
    suspend fun kakaoLogin(@Header("Authorization") token: String): Response<TokenResponseDto>

    @GET("auth/login/kakao/validate")
    suspend fun validateJwt(@Header("Authorization") token: String): Response<String>
}