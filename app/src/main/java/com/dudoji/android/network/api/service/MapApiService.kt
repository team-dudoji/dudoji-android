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
    @GET("/api/user/map_section/get")
    suspend fun getMapSections(): Response<MapSectionResponse>

    @POST("/api/user/reveal_circles/save")
    suspend fun saveCircle(@Body request:RevealCircleRequest): Response<String>
}