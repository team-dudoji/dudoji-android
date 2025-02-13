package com.dudoji.android.model.mapsection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.dudoji.android.util.mapsection.BitmapUtil
import com.dudoji.android.util.mapsection.BitmapUtil.Companion.combineBitmapInGrid
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
        if (coordinate.zoom == BASIC_ZOOM_LEVEL) {
            val mapSection = mapSections[coordinate]
            if (mapSection == null) { // MapSection is not found
                return fullBitmap
            } else { // MapSection is found
                if (mapSection is DetailedMapSection) {
                    return mapSection.GetBitmap()
                } else {
                    return emptyBitmap
                }
            }
        } else if (coordinate.zoom < BASIC_ZOOM_LEVEL) {
            return getBitmapWithCombine(coordinate)
        } else if (coordinate.zoom > BASIC_ZOOM_LEVEL) {
            return getBitmapWithCrop(coordinate)
        }
        return null
    }

    fun getBitmapWithCombine(coordinate: TileCoordinate): Bitmap { // on zoom level < BASIC_ZOOM_LEVEL
        val diffOfZoomLevel = BASIC_ZOOM_LEVEL - coordinate.zoom
        val numOfTile = 1 shl diffOfZoomLevel
        var baseBitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(baseBitmap)

        for (xOfTile in 0 until numOfTile) {
            for (yOfTile in 0 until numOfTile) {
                val childCoordinate = TileCoordinate(
                    xOfTile + numOfTile * coordinate.x,
                    yOfTile + numOfTile * coordinate.y,
                    15)
                val childMapSection = mapSections[childCoordinate]
                if (childMapSection == null) {
                    canvas.combineBitmapInGrid(fullBitmap, numOfTile, xOfTile, yOfTile)
                } else {
                    if (childMapSection is DetailedMapSection) {
                        canvas.combineBitmapInGrid(childMapSection.GetBitmap(), numOfTile, xOfTile, yOfTile)
                    } else {
                        canvas.combineBitmapInGrid(emptyBitmap, numOfTile, xOfTile, yOfTile)
                    }
                }
            }
        }
        return baseBitmap
    }

    fun getBitmapWithCrop(coordinate: TileCoordinate): Bitmap { // on zoom level > BASIC_ZOOM_LEVEL
        val diffOfZoomLevel = coordinate.zoom - BASIC_ZOOM_LEVEL
        val numOfTile = 1 shl diffOfZoomLevel

        val parentTileCoordinate = TileCoordinate(
            coordinate.x / numOfTile,
            coordinate.y / numOfTile,
            15
        )

        val xOfTile = coordinate.x - numOfTile * parentTileCoordinate.x
        val yOfTile = coordinate.y - numOfTile * parentTileCoordinate.y

        val parentMapSection = mapSections[parentTileCoordinate]

        if (parentMapSection == null) { // Bitmap is not found
            return fullBitmap
        } else { // Bitmap is found
            if (parentMapSection is DetailedMapSection) {
                return BitmapUtil.cropBitmapInGrid(parentMapSection.GetBitmap(), TILE_SIZE, numOfTile, xOfTile, yOfTile)
            } else {
                return emptyBitmap
            }
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