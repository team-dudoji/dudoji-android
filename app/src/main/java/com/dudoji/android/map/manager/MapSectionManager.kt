package com.dudoji.android.map.manager

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.config.FOG_COLOR
import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.domain.mapsection.DetailedMapSection
import com.dudoji.android.map.domain.mapsection.MapSection
import com.dudoji.android.map.utils.mapsection.BitmapUtil
import com.dudoji.android.map.utils.mapsection.BitmapUtil.Companion.combineBitmapInGrid
import com.dudoji.android.map.utils.tile.TILE_SIZE

class MapSectionManager(mapSections: List<MapSection>) {
    private val mapSections: Map<TileCoordinate, MapSection>


    private val emptyBitmap: Bitmap
    private val fullBitmap: Bitmap

    init {
        this.mapSections = mapSections.associateBy { TileCoordinate(it.x, it.y, 15) }
        emptyBitmap = BitmapUtil.createBitmapWithColor(TILE_SIZE, TILE_SIZE, Color.TRANSPARENT)
        fullBitmap = BitmapUtil.createBitmapWithColor(TILE_SIZE, TILE_SIZE, FOG_COLOR)
    }
    companion object {
        private val dirtyMapSectionCoordinates: HashSet<TileCoordinate> = HashSet()

        fun setDirtyCoordinate(coordinate: TileCoordinate) {
            val diff = BASIC_ZOOM_LEVEL - coordinate.zoom

            val baseX = (coordinate.x shl diff) - 1
            val baseY = (coordinate.y shl diff) - 1
            val tileCount = (1 shl kotlin.math.abs(diff)) + 2

            for (x in 0 until tileCount) {
                for (y in 0 until tileCount) {
                    val targetCoordinate = TileCoordinate(
                        baseX + x,
                        baseY + y,
                        BASIC_ZOOM_LEVEL
                    )
                    Log.d("MapSectionManager", "setDirtyCoordinate: $targetCoordinate")
                    dirtyMapSectionCoordinates.add(targetCoordinate)
                }
            }
        }
    }

    fun getDirtyMapSections(): List<MapSection> {
        val dirtyCoordinates = dirtyMapSectionCoordinates.mapNotNull { coordinate ->
            mapSections[coordinate] ?: DetailedMapSection(coordinate)
        }
        dirtyMapSectionCoordinates.clear()
        return dirtyCoordinates
    }

    fun getBitmap(coordinate: TileCoordinate): Bitmap? {
        if (coordinate.zoom == BASIC_ZOOM_LEVEL) {
            val mapSection = mapSections[coordinate]
            if (mapSection == null) { // MapSection is not found
                return fullBitmap
            } else { // MapSection is found
                if (mapSection is DetailedMapSection) {
                    return mapSection.getBitmap()
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
                    15
                )
                val childMapSection = mapSections[childCoordinate]
                if (childMapSection == null) {
                    canvas.combineBitmapInGrid(fullBitmap, numOfTile, xOfTile, yOfTile)
                } else {
                    if (childMapSection is DetailedMapSection) {
                        canvas.combineBitmapInGrid(childMapSection.getBitmap(), numOfTile, xOfTile, yOfTile)
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
                return BitmapUtil.cropBitmapInGrid(parentMapSection.getBitmap(), TILE_SIZE, numOfTile, xOfTile, yOfTile)
            } else {
                return emptyBitmap
            }
        }
    }
}