package com.dudoji.android.map.utils.tile

import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.map.domain.TileCoordinate
import kotlin.math.cos
import kotlin.math.ln

const val EARTH_RADIUS = 6378137.0

// for google tile system's positioning (by x, y, zoom)
class TileCoordinateUtil {
    companion object {

        // get basic tile coordinate (basic tile coordinate is the tile coordinate at BASIC_ZOOM_LEVEL)
        fun getBasicTileCoordinate(tileCoordinate: TileCoordinate): TileCoordinate {
            if (tileCoordinate.zoom == BASIC_ZOOM_LEVEL) {
                return tileCoordinate
            } else {
                val diffOfZoomLevel = Math.abs(BASIC_ZOOM_LEVEL - tileCoordinate.zoom)
                val numOfTile = 1 shl diffOfZoomLevel
                if (tileCoordinate.zoom < BASIC_ZOOM_LEVEL) {
                    val x = tileCoordinate.x * numOfTile
                    val y = tileCoordinate.y * numOfTile
                    return TileCoordinate(x, y, BASIC_ZOOM_LEVEL)
                } else if (tileCoordinate.zoom > BASIC_ZOOM_LEVEL) {
                    val x = tileCoordinate.x / numOfTile
                    val y = tileCoordinate.y / numOfTile
                    return TileCoordinate(x, y, BASIC_ZOOM_LEVEL)
                }
            }
            throw Exception("getBasicTileCoordinate Error")
        }

        fun getCloseBasicTileCoordinates(tileCoordinate: TileCoordinate, closeRange: Int = 1): List<TileCoordinate> {
            val basicTileCoordinate = getBasicTileCoordinate(tileCoordinate)
            val closeBasicTileCoordinates = mutableListOf<TileCoordinate>()
            val maxClose = (if (tileCoordinate.zoom >= BASIC_ZOOM_LEVEL) 1 else (1 shl (BASIC_ZOOM_LEVEL - tileCoordinate.zoom)))

            for (i in -closeRange until maxClose + closeRange) {
                for (j in  -closeRange until maxClose + closeRange) {
                    val x = basicTileCoordinate.x + i
                    val y = basicTileCoordinate.y + j
                    closeBasicTileCoordinates.add(TileCoordinate(x, y, BASIC_ZOOM_LEVEL))
                }
            }

            return closeBasicTileCoordinates
        }

        // lat/lng → EPSG:3857 (world coordinate)
        fun latLngToWorld(lat: Double, lng: Double): Pair<Double, Double> {
            var siny = Math.sin((lat * Math.PI) / 180);
            siny = Math.min(Math.max(siny, -0.9999), 0.9999);

            val x = TILE_SIZE * (0.5 + lng / 360)
            val y = TILE_SIZE * (0.5 - ln((1 + siny) / (1 - siny)) / (4 * Math.PI))
            return Pair(x, y)
        }

        fun yOfWorldToLat(yOfWorld: Double): Double {
            val lat = 180 / Math.PI * (2 * Math.atan(Math.exp(yOfWorld / TILE_SIZE * 2 * Math.PI)) - Math.PI / 2)
            return lat
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

        fun pixelToPixelInTile(xOfPixel: Int, yOfPixel: Int, tileCoordinate: TileCoordinate): Pair<Int, Int> {
            val x = xOfPixel - tileCoordinate.x * TILE_SIZE
            val y = yOfPixel - tileCoordinate.y * TILE_SIZE
            return Pair(x, y)
        }

        fun meterToPixel(length: Double, lat: Double, zoomLevel: Int): Double {
            return length / (
                    cos(Math.toRadians(lat)) * 2.0 * Math.PI * EARTH_RADIUS
                     / (TILE_SIZE * (1 shl zoomLevel))

                    )
        }
    }
}