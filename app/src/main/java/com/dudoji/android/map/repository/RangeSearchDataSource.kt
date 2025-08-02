package com.dudoji.android.map.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.map.utils.MapUtil
import com.dudoji.android.network.api.dto.BaseDto
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

abstract class RangeSearchDataSource<T:BaseDto<D>, D> {
    val dataList = mutableListOf<D>()

    private var lastUpdatedLatLng: LatLng? = null

    protected abstract suspend fun fetchFromApi(
        lat: Double,
        lng: Double,
        radius: Double
    ): Response<List<T>>

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun load(latLng: LatLng, radius: Double): Boolean{
        if (lastUpdatedLatLng == null ||
            MapUtil.distanceBetween(lastUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            val response = fetchFromApi(
                latLng.latitude,
                latLng.longitude,
                radius)
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