package com.dudoji.android.util.tile

// for google tile system's positioning (by x, y, zoom)
class TilePositionUtil {
    companion object {
        // Convert Google Map Tile Position to LatLng
        // (x, y), (x+1, y+1) rectangle is tile area
        fun tilePositionToLatLng(x: Int, y: Int, zoom: Int): Pair<Double, Double> {
            val n = getTileSize(zoom)
            val lng_deg = x / n * 360.0 - 180.0
            val lat_rad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)))
            val lat_deg = Math.toDegrees(lat_rad)
            return Pair(lat_deg, lng_deg)
        }

        // Get Tile Size by zoom level
        fun getTileSize(zoom: Int): Double {
            return Math.pow(2.0, zoom.toDouble())
        }
    }
}