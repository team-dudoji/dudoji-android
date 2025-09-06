package com.dudoji.android.map.repository

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
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

    fun distanceBetween(p1: LatLng, p2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        return results[0].toDouble()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun load(latLng: LatLng, radius: Double): Boolean{
        Log.d(this.javaClass.name, "try load: $latLng, last: $lastUpdatedLatLng")
        if (lastUpdatedLatLng == null ||
            distanceBetween(lastUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            Log.d(this.javaClass.name, "load: $latLng, last: $lastUpdatedLatLng")
            val response = fetchFromApi(
                latLng.latitude,
                latLng.longitude,
                radius)
            if (response.isSuccessful) {
                val datas = response.body()
                Log.d(this.javaClass.name, "datas: $datas")
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