package com.dudoji.android.map.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.network.api.dto.BaseDto
import com.dudoji.android.network.api.service.RangeSearchApiService
import com.google.android.gms.maps.model.LatLng

open class RangeSearchDataSource<T:BaseDto<D>, D>(val apiService: RangeSearchApiService<T, D>) {
    val dataList = mutableListOf<D>()

    private var lastUpdatedLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun load(latLng: LatLng, radius: Double): Boolean{
        if (lastUpdatedLatLng == null ||
            MapUtil.distanceBetween(lastUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            val response = apiService.getRangeSearchResults(
                radius.toInt(),
                latLng.latitude,
                latLng.longitude)
            if (response.isSuccessful) {
                val datas = response.body()
                dataList.clear()
                dataList.addAll(datas?.map {
                        it.toDomain()
                    } ?: emptyList())
                lastUpdatedLatLng = latLng
                return true
            } else {
                Log.e(this.javaClass.name, "Failed to fetch Data: ${response.message()}, error: ${response.errorBody()?.string()}")
            }
        }
        return false
    }
}