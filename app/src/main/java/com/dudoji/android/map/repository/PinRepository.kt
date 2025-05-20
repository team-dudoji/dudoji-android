package com.dudoji.android.map.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.config.PIN_UPDATE_THRESHOLD
import com.dudoji.android.follow.repository.FollowRepository
import com.dudoji.android.map.domain.pin.Pin
import com.dudoji.android.map.domain.pin.Who
import com.dudoji.android.map.utils.MapUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

object PinRepository {
    private val pinList = mutableListOf<Pin>()
    private lateinit var googleMap: GoogleMap

    private var lastPinUpdatedLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadPins(latLng: LatLng, radius: Double): Boolean{
        if (lastPinUpdatedLatLng == null ||
            MapUtil.distanceBetween(lastPinUpdatedLatLng!!, latLng) > PIN_UPDATE_THRESHOLD) {
            val response = RetrofitClient.pinApiService.getPins(
                radius.toInt(),
                latLng.latitude,
                latLng.longitude)
            if (response.isSuccessful) {
                val pins = response.body()
                FollowRepository.getFollowings() //팔로잉 정보 로딩
                
                pinList.clear()
                pinList.addAll(pins?.map { pinDto ->
                    pinDto.toDomain()
                    } ?: emptyList()
                )
                lastPinUpdatedLatLng = latLng
                return true
            } else {
                Log.e("PinRepository", "Failed to fetch pins: ${response.errorBody()?.string()}")
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addPin(pin: Pin): Boolean {
        val response = RetrofitClient.pinApiService.createPin(pin.toPinDto())
        if (response.isSuccessful) {
            pinList.add(pin)
            return true
        }
        Log.e("PinRepository", "Failed to add pin: ${response.errorBody()?.string()}")
        return false
    }

//    fun updateFilter(pinApplier: PinApplier) {
//        pinApplier.clearPins()
//        val visibleFriendId: HashSet<Long> = FollowRepository.getFollowings()
//            .filter { it.isVisible }
//            .map { it.user.id }
//            .toHashSet()
//        Log.d("PinRepository", "Visible Friend IDs: $visibleFriendId")
//        pinList.forEach { pin ->
//            if (visibleFriendId.contains(pin.userId)) {
//                pinApplier.applyPin(pin)
//            }
//        }
//    }

    fun getPins(): List<Pin> {
        return pinList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun injectTestPins() {
        val now = LocalDateTime.now()
        pinList.clear()
        pinList.addAll(
            listOf(
                Pin(37.0, 127.0, 1L, now, "내 핀", "내용", Who.MINE),
                Pin(37.1, 127.1, 2L, now, "친구 핀", "내용", Who.FOLLOWING),
                Pin(37.2, 127.2, 3L, now, "모르는 핀", "내용", Who.UNKNOWN)
            )
        )
        Log.d("PinRepository", "✅ 테스트 핀 3개 주입 완료")
    }




}