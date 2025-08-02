package com.dudoji.android.landmark.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.landmark.api.dto.LandmarkResponseDto
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.repository.RangeSearchDataSource
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
object LandmarkDataSource: RangeSearchDataSource<LandmarkResponseDto, Landmark>() {

    fun getLandmarks(): List<Landmark> {
         return dataList
    }

    override suspend fun fetchFromApi(
        lat: Double,
        lng: Double,
        radius: Double
    ): Response<List<LandmarkResponseDto>> {
        RetrofitClient.landmarkApiService.getRangeSearchResults(
            radius.toInt(),
            lat,
            lng
        ).let { response ->
            if (response.isSuccessful) {
                return response
            } else {
                throw Exception("Failed to fetch landmarks: ${response.errorBody()?.string()}")
            }
        }
    }
}