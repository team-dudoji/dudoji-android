package com.dudoji.android.map.domain

import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.map.utils.tile.TileCoordinateUtil

data class RevealCircle(val lat: Double, val lng: Double, val radius: Double) {
    fun toWorldPosition(): WorldPosition {
        val worldCoordinate: Pair<Double, Double> = TileCoordinateUtil.Companion.latLngToWorld(lat, lng)
        return WorldPosition(worldCoordinate.first, worldCoordinate.second, radius.toInt())
    }

    fun getTileCoordinate(): TileCoordinate {
        val worldCoordinate: Pair<Double, Double> = TileCoordinateUtil.Companion.latLngToWorld(lat, lng)
        val pixelCoordinate = TileCoordinateUtil.Companion.worldToPixel(worldCoordinate.first, worldCoordinate.second, BASIC_ZOOM_LEVEL)
        val tileCoordinate = TileCoordinateUtil.Companion.pixelToTile(pixelCoordinate.first, pixelCoordinate.second)
        return TileCoordinate(tileCoordinate.first, tileCoordinate.second, BASIC_ZOOM_LEVEL)
    }
}
