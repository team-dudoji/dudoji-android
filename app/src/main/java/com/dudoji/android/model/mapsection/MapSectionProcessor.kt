package com.dudoji.android.model.mapsection

import android.util.Log

// Control Map Sections Collection
@Deprecated(COORDINATE_CHANGE_WARNING_TEXT)
class MapSectionProcessor(private val mapSections: List<MapSection>) {

    companion object {
        const val DETAIL_PROCESSING_RATE = 2
        const val EXPLORED_RATE_THRESHOLD = 0.5f
    }

    fun getBit(minLat: Double, minLng : Double, size: Double): Boolean {
        if ((size / MapSection.MAPSECTION_LATLNG_SIZE) < DETAIL_PROCESSING_RATE) {
            return getBitDetail(minLat, minLng, size)
        } else {
            return getBitRoughly(minLat, minLng, size)
        }
    }

    // get bit in detail (by map section's bit map)
    private fun getBitDetail(minLat: Double, minLng : Double, size: Double): Boolean {
        // TODO: implement this function
        return false
    }

    // get bit roughly (by map section's explored rate)
    private fun getBitRoughly(minLat: Double, minLng : Double, size: Double): Boolean {
        val maxLat = minLat + size
        val maxLng = minLng + size

        val minDudojiX = Math.floor((minLng - MapSection.BASE_LNG) / MapSection.MAPSECTION_LATLNG_SIZE).toInt()
        val minDudojiY = Math.floor((minLat - MapSection.BASE_LAT) / MapSection.MAPSECTION_LATLNG_SIZE).toInt()
        val maxDudojiX = Math.ceil((maxLng - MapSection.BASE_LNG) / MapSection.MAPSECTION_LATLNG_SIZE).toInt()
        val maxDudojiY = Math.ceil((maxLat - MapSection.BASE_LAT) / MapSection.MAPSECTION_LATLNG_SIZE).toInt()

        var totalExplorationRate = 0.0f
        var numOfMapSections = (maxDudojiX - minDudojiX + 1) * (maxDudojiY - minDudojiY + 1)

        for (dudojiX in minDudojiX until maxDudojiX+1) {
            for (dudojiY in minDudojiY until maxDudojiY+1) {
                val mapSection = getMapSection(dudojiX, dudojiY)
                if (mapSection != null) {
                    Log.w("getBit", "dudiji x: $dudojiX, dudiji y: $dudojiY, rate: ${mapSection.getExploredRate()}")
                    totalExplorationRate += mapSection.getExploredRate() / numOfMapSections
                }
            }
        }
        Log.w("getBit", "total rate: $totalExplorationRate")
        return totalExplorationRate >= EXPLORED_RATE_THRESHOLD
    }

    // get map section by dudojiX and dudojiY
    fun getMapSection(x: Int, y: Int): MapSection? {
        for (mapSection in mapSections) {
            if (mapSection.x == x && mapSection.y == y) {
                return mapSection
            }
        }
        return null
    }
}