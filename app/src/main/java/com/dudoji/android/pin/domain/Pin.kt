package com.dudoji.android.pin.domain

    import com.dudoji.android.pin.api.dto.PinDto
    import com.google.android.gms.maps.model.LatLng
    import com.google.maps.android.clustering.ClusterItem
    import java.time.LocalDateTime

    data class Pin (
        val lat: Double,
        val lng: Double,
        val pinId: Long,
        val userId: Long,
        val createdDate: LocalDateTime,
        @get:JvmName("getPinTitle")
        val title: String,
        val content: String,
        val master: Who
    ) : ClusterItem {
        override fun getPosition(): LatLng {
            return LatLng(lat, lng)
        }

        override fun getTitle(): String? {
            return title
        }

        override fun getSnippet(): String? {
            return content
        }
        fun toPinDto(): PinDto {
            return PinDto(
                lat = lat,
                lng = lng,
                pinId = pinId,
                userId = userId,
                createdDate = createdDate,
                title = title,
                content = content,
                master = master
            )
        }
    }