package com.dudoji.android.network.api.service


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

// User Data API endpoints
interface UserApiService {
    @GET("auth/login/kakao/app-login")
    suspend fun kakaoLogin(@Header("Authorization") token: String): Response<Void>
}