package com.dudoji.android.pin.api.service

import com.dudoji.android.pin.api.dto.PinRequestDto
import com.dudoji.android.pin.api.dto.PinResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PinApiService {
    @POST("/api/user/pin/")
    suspend fun createPin(@Body pin: PinRequestDto): Response<PinResponseDto>

    @Multipart
    @POST("/api/user/images")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<String>

    @GET("/api/user/pin/")
    suspend fun getPins(@Query("radius") radius: Int,
                     @Query("lat") lat: Double,
                     @Query("lng") lng: Double): Response<List<PinResponseDto>>

    @POST("/api/user/pin/{pinId}/like")
    suspend fun likePin(@Path("pinId") pinId: Long): Response<String>

    @DELETE("/api/user/pin/{pinId}/like")
    suspend fun unlikePin(@Path("pinId") pinId: Long): Response<String>

    @GET("/api/user/pin/mine")
    suspend fun getMyPins(): Response<List<PinResponseDto>>
}