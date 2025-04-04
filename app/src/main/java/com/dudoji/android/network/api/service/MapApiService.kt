package com.dudoji.android.network.api.service

import com.dudoji.android.network.entity.MapSectionResponse
import com.dudoji.android.network.entity.revealcircle.RevealCircleRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Map Data API endpoints
// ex) MapSections, RevealCircle
interface MapApiService {
    @GET("/api/user/map_section/get")
    suspend fun getMapSections(): MapSectionResponse

    @POST("/api/user/reveal_circle/save")
    suspend fun saveCircle(@Body request:RevealCircleRequest): Boolean
}