package com.dudoji.android.pin.api.service

import com.dudoji.android.pin.api.dto.PinDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PinApiService {
    @POST("/api/user/pin/")
    suspend fun createPin(@Body pin: PinDto): Response<String>

    @GET("/api/user/pin/")
    suspend fun getPins(@Query("radius") radius: Int,
                     @Query("lat") lat: Double,
                     @Query("lng") lng: Double): Response<List<PinDto>>

    @POST("/api/user/pin/{pinId}/like")
    suspend fun likePin(@Path("pinId") pinId: Long): Response<String>

    @DELETE("/api/user/pin/{pinId}/like")
    suspend fun unlikePin(@Path("pinId") pinId: Long): Response<String>
}