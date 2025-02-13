package com.dudoji.android.model.mapsection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.dudoji.android.util.tile.TILE_SIZE

data class TileCoordinate(val x: Int, val y: Int, val zoom: Int)

class MapSectionManager(mapSections: List<MapSection>) {
    private val mapSections: Map<TileCoordinate, MapSection>

    private val emptyBitmap: Bitmap
    private val fullBitmap: Bitmap

    init {
        this.mapSections = mapSections.associateBy { TileCoordinate(it.x, it.y, 15) }
        emptyBitmap = createBitmapWithColor(TILE_SIZE, TILE_SIZE, Color.TRANSPARENT)
        fullBitmap = createBitmapWithColor(TILE_SIZE, TILE_SIZE, Color.BLACK)
    }

    fun getBitmap(coordinate: TileCoordinate): Bitmap? {
        if (coordinate.zoom == 15) {
            Log.w("MapSectionManager", "getBitmap: ${coordinate.x}, ${coordinate.y}, ${coordinate.zoom} is requested")
            val mapSection = mapSections[coordinate]
            if (mapSection == null) {
                return fullBitmap
            } else {
                Log.w("MapSectionManager", "getBitmap: ${mapSection.x}, ${mapSection.y} is found")
                if (mapSection is DetailedMapSection) {
                    return mapSection.GetBitmap()
                } else {
                    return emptyBitmap
                }
            }
        } else {
            // TODO: Implement zoom level change
            return fullBitmap
        }
    }

    fun createBitmapWithColor(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

}