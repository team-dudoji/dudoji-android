package com.dudoji.android.landmark.api.service

import com.dudoji.android.landmark.api.dto.LandmarkResponseDto
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.network.api.service.RangeSearchApiService
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LandmarkApiService: RangeSearchApiService<LandmarkResponseDto, Landmark> {
    @GET("/api/user/landmarks")
    override suspend fun getRangeSearchResults(@Query("radius") radius: Int,
                        @Query("lat") lat: Double,
                        @Query("lng") lng: Double): Response<List<LandmarkResponseDto>>

    @GET("/api/user/landmarks/search")
    suspend fun searchLandmarks(@Query("keyword") keyword: String): Response<List<LandmarkResponseDto>>
}