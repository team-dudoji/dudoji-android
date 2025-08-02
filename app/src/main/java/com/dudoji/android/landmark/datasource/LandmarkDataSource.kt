package com.dudoji.android.landmark.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.landmark.api.dto.LandmarkResponseDto
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.map.repository.RangeSearchDataSource

@RequiresApi(Build.VERSION_CODES.O)
object LandmarkDataSource: RangeSearchDataSource<LandmarkResponseDto, Landmark>(RetrofitClient.landmarkApiService) {
    fun getLandmarks(): List<Landmark> {
         return dataList
    }
}