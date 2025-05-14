package com.dudoji.android.network.api.service

import com.dudoji.android.network.dto.MapSectionResponse
import com.dudoji.android.network.dto.revealcircle.RevealCircleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Map Data API endpoints
// ex) MapSections, RevealCircle
interface MapApiService {
    @GET("/api/user/map-sections")
    suspend fun getMapSections(): Response<MapSectionResponse>

    @POST("/api/user/reveal-circles")
    suspend fun saveCircle(@Body request:RevealCircleRequest): Response<String>
}