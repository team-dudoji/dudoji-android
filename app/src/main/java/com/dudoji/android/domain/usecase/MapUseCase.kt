package com.dudoji.android.domain.usecase

import RetrofitClient
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dudoji.android.domain.repository.LocationRepository
import com.dudoji.android.pin.api.dto.PinRequestDto
import com.dudoji.android.pin.repository.PinRepository
import com.dudoji.android.pin.util.PinMakeData
import com.dudoji.android.util.UriConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class MapUseCase @Inject constructor(
    val locationRepository: LocationRepository,
    @ApplicationContext val context: Context
) {
    fun getLocationUpdates(): StateFlow<Location> {
        return locationRepository.getLocationUpdates()
    }
    fun getBearing(): StateFlow<Float> {
        return locationRepository.getBearing()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createPin(pinMakeData: PinMakeData): Boolean {

        val imageUrl = saveImage(pinMakeData.imageUri)

        if (imageUrl.isEmpty()) {
            return false
        }

        val requestDto =
            PinRequestDto(
                content = pinMakeData.content,
                createdDate = LocalDateTime.of(pinMakeData.date, LocalDateTime.now().toLocalTime()),
                imageUrl = imageUrl,
                lat = pinMakeData.lat,
                lng = pinMakeData.lng,
                address = pinMakeData.address,
                placeName = pinMakeData.placeName,
                pinSkinId = pinMakeData.pinSkinId,
                hashtags = pinMakeData.hashtags
            )

        val isSuccessful = PinRepository.addPin(requestDto)

        Toast.makeText(
            context,
            if (isSuccessful) "핀 추가에 성공했습니다." else "핀 추가에 실패했습니다.",
            Toast.LENGTH_SHORT
        ).show()

        return isSuccessful
    }

    suspend fun saveImage(imageUri: Uri): String {
        val image = UriConverter.uriToMultipartBodyPart(
            context,
            imageUri
        )

        val imageResponse = RetrofitClient.pinApiService.uploadImage(image)

        if (!imageResponse.isSuccessful) {
            Log.e("PinRepository", "Failed to upload image: ${imageResponse.errorBody()?.string()}")
            Toast.makeText(
                context,
                "핀 추가에 실패했습니다.",
                Toast.LENGTH_SHORT
            ).show()
            return ""
        }
        return imageResponse.body()!!
    }
}