package com.dudoji.android.util.tile

import kotlin.math.ln

// for google tile system's positioning (by x, y, zoom)
class TileCoordinateUtil {
    companion object {
        // lat/lng → EPSG:3857 (world coordinate)
        fun latLngToWorld(lat: Double, lng: Double): Pair<Double, Double> {
            var siny = Math.sin((lat * Math.PI) / 180);
            siny = Math.min(Math.max(siny, -0.9999), 0.9999);

            val x = TILE_SIZE * (0.5 + lng / 360)
            val y = TILE_SIZE * (0.5 - ln((1 + siny) / (1 - siny)) / (4 * Math.PI))
            return Pair(x, y)
        }

        // world → pixel
        fun worldToPixel(xOfWorld: Double, yOfWorld: Double, zoomLevel: Int): Pair<Int, Int> {
            val x: Int = (xOfWorld * (1 shl zoomLevel)).toInt()
            val y: Int = (yOfWorld * (1 shl zoomLevel)).toInt()
            return Pair(x, y)
        }

        // pixel → tile
        fun pixelToTile(xOfPixel: Int, yOfPixel: Int): Pair<Int, Int> {
            val tileX = xOfPixel / TILE_SIZE
            val tileY = yOfPixel / TILE_SIZE
            return Pair(tileX, tileY)
        }

        // pixel to Pixel in tile
        fun pixelToPixelInTile(xOfPixel: Int, yOfPixel: Int): Pair<Int, Int> {
            val x = xOfPixel % TILE_SIZE
            val y = yOfPixel % TILE_SIZE
            return Pair(x, y)
        }
    }
}