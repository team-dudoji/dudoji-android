    package com.dudoji.android.map.domain.pin

    import com.dudoji.android.network.dto.PinDto
    import com.google.android.gms.maps.model.LatLng
    import com.google.maps.android.clustering.ClusterItem
    import java.time.LocalDateTime

    data class Pin (
        val lat: Double,
        val lng: Double,
    //    val pinId: Long,
        val userId: Long,
        val createdDate: LocalDateTime,
        @get:JvmName("getPinTitle")
        val title: String,
        val content: String,
        val master: Who // 기본값 마이핀
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
                userId = userId,
                createdDate = createdDate,
                title = title,
                content = content,
                master = master
            )
        }
    }