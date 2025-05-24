package com.dudoji.android.pin.domain

    import com.dudoji.android.pin.api.dto.PinRequestDto
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.time.LocalDate

data class Pin (
    val lat: Double,
    val lng: Double,
    val pinId: Long,
    val userId: Long,
    val likeCount: Int,
    val isLiked: Boolean,
    val imageUrl: String,
    val createdDate: LocalDate,
    val content: String,
    val master: Who
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return "pin"
    }

    override fun getSnippet(): String? {
        return content
    }

    fun toPinRequestDto(): PinRequestDto {
        return PinRequestDto(
            lat = lat,
            lng = lng,
            createdDate = createdDate,
            content = content,
            imageUrl = imageUrl
        )
    }
}