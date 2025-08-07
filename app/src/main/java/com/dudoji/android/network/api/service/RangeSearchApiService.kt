package com.dudoji.android.network.api.service

import com.dudoji.android.network.api.dto.BaseDto
import retrofit2.Response

interface RangeSearchApiService<T: BaseDto<D>, D> {
    suspend fun getRangeSearchResults(
        radius: Int,
        lat: Double,
        lng: Double
    ): Response<List<T>>
}