package com.dudoji.android.map.manager

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.config.FOG_COLOR
import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.domain.mapsection.MapSection
import com.dudoji.android.map.utils.mapsection.BitmapUtil
import com.dudoji.android.map.utils.mapsection.BitmapUtil.Companion.combineBitmapInGrid
import com.dudoji.android.map.utils.tile.TILE_SIZE

abstract class MapSectionManager {
    protected val emptyBitmap: Bitmap
    protected val fullBitmap: Bitmap

    init {
        emptyBitmap = BitmapUtil.createBitmapWithColor(TILE_SIZE, TILE_SIZE, Color.TRANSPARENT, false)
        fullBitmap = BitmapUtil.createBitmapWithColor(TILE_SIZE, TILE_SIZE, FOG_COLOR, false)
    }

    abstract fun getMapSection(coordinate: TileCoordinate): MapSection?
    
    fun getBitmap(coordinate: TileCoordinate): Bitmap? {
        if (coordinate.zoom == BASIC_ZOOM_LEVEL) {
            val mapSection = getMapSection(coordinate)
            if (mapSection == null) { // MapSection is not found
                return fullBitmap
            } else { // MapSection is found
                mapSection.bitmap ?: emptyBitmap
            }
        } else if (coordinate.zoom < BASIC_ZOOM_LEVEL) {
            return getBitmapWithCombine(coordinate)
        } else if (coordinate.zoom > BASIC_ZOOM_LEVEL) {
            return getBitmapWithCrop(coordinate)
        }
        return fullBitmap
    }

    private fun getBitmapWithCombine(coordinate: TileCoordinate): Bitmap { // on zoom level < BASIC_ZOOM_LEVEL
        val diffOfZoomLevel = BASIC_ZOOM_LEVEL - coordinate.zoom
        val numOfTile = 1 shl diffOfZoomLevel
        var baseBitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(baseBitmap)

        for (xOfTile in 0 until numOfTile) {
            for (yOfTile in 0 until numOfTile) {
                val childCoordinate = TileCoordinate(
                    xOfTile + numOfTile * coordinate.x,
                    yOfTile + numOfTile * coordinate.y,
                    15
                )
                val childMapSection = getMapSection(childCoordinate)
                if (childMapSection == null) {
                    canvas.combineBitmapInGrid(fullBitmap, numOfTile, xOfTile, yOfTile)
                } else {
                    canvas.combineBitmapInGrid(
                        childMapSection.bitmap ?: emptyBitmap, numOfTile, xOfTile, yOfTile)
                }
            }
        }
        return baseBitmap
    }

    private fun getBitmapWithCrop(coordinate: TileCoordinate): Bitmap { // on zoom level > BASIC_ZOOM_LEVEL
        val diffOfZoomLevel = coordinate.zoom - BASIC_ZOOM_LEVEL
        val numOfTile = 1 shl diffOfZoomLevel

        val parentTileCoordinate = TileCoordinate(
            coordinate.x / numOfTile,
            coordinate.y / numOfTile,
            15
        )

        val xOfTile = coordinate.x - numOfTile * parentTileCoordinate.x
        val yOfTile = coordinate.y - numOfTile * parentTileCoordinate.y

        val parentMapSection = getMapSection(parentTileCoordinate)

        if (parentMapSection == null) { // Bitmap is not found
            return fullBitmap
        } else { // Bitmap is found
            if (parentMapSection.bitmap != null) {
                return BitmapUtil.cropBitmapInGrid(parentMapSection.bitmap!!, TILE_SIZE, numOfTile, xOfTile, yOfTile)
            } else {
                return emptyBitmap
            }
        }
    }
}