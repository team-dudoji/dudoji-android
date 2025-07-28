package com.dudoji.android.landmark.api.service

import com.dudoji.android.landmark.api.dto.LandmarkResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LandmarkApiService {
    @GET("/api/user/pins")
    suspend fun getLandmarks(@Query("radius") radius: Int,
                        @Query("lat") lat: Double,
                        @Query("lng") lng: Double): Response<List<LandmarkResponseDto>>
}