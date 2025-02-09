package com.dudoji.android.model.mapsection

class MapSectionProcessor(private val mapSections: List<MapSection>) {

    companion object {
        const val DETAIL_PROCESSING_RATE = 2
    }


    fun getBit(minLat: Double, minLng : Double, size: Double): Boolean {
        if ((size / MapSection.MAPSECTION_LATLNG_SIZE) < DETAIL_PROCESSING_RATE) {
            return getBitDetail(minLat, minLng, size)
        } else {
            return getBitRoughly(minLat, minLng, size)
        }
    }

    private fun getBitDetail(minLat: Double, minLng : Double, size: Double): Boolean {
        // TODO: implement this function
        return false
    }

    private fun getBitRoughly(minLat: Double, minLng : Double, size: Double): Boolean {
        // TODO: implement this function
        return false
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