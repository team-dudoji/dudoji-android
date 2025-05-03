package com.dudoji.android.map.utils.tile.mask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.map.domain.RevealCircle
import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.repository.RevealCircleRepository
import com.dudoji.android.map.utils.location.IRevealCircleListener
import com.dudoji.android.map.utils.tile.TileCoordinateUtil

data class WorldPosition(val xOfWold: Double, val yOfWorld: Double, val radius: Int)

class PositionsMaskTileMaker<MaskTileMaker: IMaskTileMaker>(private val maskTileMaker: MaskTileMaker): IMaskTileMaker, IRevealCircleListener {

    val worldPositions: MutableMap<TileCoordinate, MutableList<WorldPosition>> = mutableMapOf()
    val positionsEdited: MutableMap<TileCoordinate, Boolean> = mutableMapOf()

    init {
        RevealCircleRepository.revealCircleListenerCaller.addListener(this)
    }

    fun addPosition(lat: Double, lng: Double, radius: Double) {
        val worldCoordinate: Pair<Double, Double> = TileCoordinateUtil.Companion.latLngToWorld(lat, lng)
        val pixelCoordinate = TileCoordinateUtil.Companion.worldToPixel(worldCoordinate.first, worldCoordinate.second, BASIC_ZOOM_LEVEL)
        val tileCoordinate = TileCoordinateUtil.Companion.pixelToTile(pixelCoordinate.first, pixelCoordinate.second)
        val tile = TileCoordinate(tileCoordinate.first, tileCoordinate.second, BASIC_ZOOM_LEVEL)
        val positions = worldPositions[tile] ?: mutableListOf()
        positions.add(WorldPosition(worldCoordinate.first, worldCoordinate.second, radius.toInt()))
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
        val coordinates = TileCoordinateUtil.Companion.getCloseBasicTileCoordinates(tileCoordinate, 4)
        for (coordinate in coordinates) {
            for (position in worldPositions[coordinate] ?: continue) {
                canvas.applyPosition(tileCoordinate, position.xOfWold, position.yOfWorld, position.radius)
            }
        }
    }

    private fun Canvas.applyPosition(tileCoordinate: TileCoordinate, xOfWold: Double, yOfWorld: Double, radius: Int) {
        val pixelCoordinate = TileCoordinateUtil.Companion.worldToPixel(xOfWold, yOfWorld, tileCoordinate.zoom)
        val pixelInTile = TileCoordinateUtil.Companion.pixelToPixelInTile(pixelCoordinate.first, pixelCoordinate.second, tileCoordinate)
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        paint.style = Paint.Style.FILL

        drawCircle(
            pixelInTile.first.toFloat(),
            pixelInTile.second.toFloat(),
            (TileCoordinateUtil.Companion.meterToPixel(radius.toDouble(), TileCoordinateUtil.Companion.yOfWorldToLat(yOfWorld), tileCoordinate.zoom)).toFloat(),
            paint)
    }

    override fun onRevealCircleAdded(revealCircle: RevealCircle) {
        addPosition(revealCircle.lat, revealCircle.lng, revealCircle.radius)
    }
}