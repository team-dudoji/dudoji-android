package com.dudoji.android.network.api.service

import com.dudoji.android.mypage.dto.LandmarkDto
import retrofit2.Response
import retrofit2.http.GET

interface LandmarkApiService {
    @GET("/api/landmarks")
    suspend fun getLandmarks(): Response<List<LandmarkDto>>
}