package com.dudoji.android.util.tile.mask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.dudoji.android.model.mapsection.BASIC_ZOOM_LEVEL
import com.dudoji.android.model.mapsection.TileCoordinate
import com.dudoji.android.util.tile.TileCoordinateUtil

data class WorldPosition(val xOfWold: Double, val yOfWorld: Double, val radius: Int)

class PositionsMaskTileMaker<MaskTileMaker: IMaskTileMaker>(private val maskTileMaker: MaskTileMaker): IMaskTileMaker {

    val worldPositions: MutableMap<TileCoordinate, MutableList<WorldPosition>> = mutableMapOf()
    val positionsEdited: MutableMap<TileCoordinate, Boolean> = mutableMapOf()

    fun addPosition(lat: Double, lng: Double, radius: Int) {
        val worldCoordinate: Pair<Double, Double> = TileCoordinateUtil.latLngToWorld(lat, lng)
        val pixelCoordinate = TileCoordinateUtil.worldToPixel(worldCoordinate.first, worldCoordinate.second, BASIC_ZOOM_LEVEL)
        val tileCoordinate = TileCoordinateUtil.pixelToTile(pixelCoordinate.first, pixelCoordinate.second)
        val tile = TileCoordinate(tileCoordinate.first, tileCoordinate.second, BASIC_ZOOM_LEVEL)
        val positions = worldPositions[tile] ?: mutableListOf()
        positions.add(WorldPosition(worldCoordinate.first, worldCoordinate.second, radius))
        worldPositions[tile] = positions
        positionsEdited[tile] = true
    }

    override suspend fun createMaskTile(x: Int, y: Int, zoom: Int
    ): Bitmap {
        val bitmap = maskTileMaker.createMaskTile(x, y, zoom)
        val positionAppliedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(positionAppliedBitmap)
        canvas.drawBitmap(bitmap, 0F, 0F, null)
        applyPositions(canvas, TileCoordinate(x, y, zoom))
        return positionAppliedBitmap
    }

    private fun applyPositions(canvas: Canvas, tileCoordinate: TileCoordinate) {
        Log.w("PositionsMaskTileMaker", "applyPositions: $tileCoordinate")
        val coordinates = TileCoordinateUtil.getCloseBasicTileCoordinates(tileCoordinate)
        for (coordinate in coordinates) {
            for (position in worldPositions[coordinate] ?: continue) {
                canvas.applyPosition(position.xOfWold, position.yOfWorld, BASIC_ZOOM_LEVEL, position.radius)
            }
        }
    }

    private fun Canvas.applyPosition(lat: Double, lng: Double, zoom: Int, radius: Int) {
        val worldCoordinate: Pair<Double, Double> = TileCoordinateUtil.latLngToWorld(lat, lng)
        val pixelCoordinate = TileCoordinateUtil.worldToPixel(worldCoordinate.first, worldCoordinate.second, zoom)
        val pixelInCoordinate = TileCoordinateUtil.pixelToPixelInTile(pixelCoordinate.first, pixelCoordinate.second)
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        paint.style = Paint.Style.FILL

        drawCircle(
            100F,
            100F,
            (TileCoordinateUtil.meterToPixelRate(lat, zoom) * radius).toFloat(),
            paint)
    }
}