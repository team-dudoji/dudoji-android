package com.dudoji.android.map.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.core.graphics.get
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.config.FOG_COLOR
import com.dudoji.android.config.GRADIENT_RADIUS_RATE
import com.dudoji.android.config.TILE_SIZE
import com.dudoji.android.map.utils.tile.TileCoordinateUtil

open class MapSection {
    val x : Int
    val y : Int
    var bitmap : Bitmap? = null
    var _canvas: Canvas? = null
    val canvas: Canvas
        get() {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)
                _canvas = Canvas(bitmap!!)
                _canvas!!.drawColor(FOG_COLOR)
            } else if (_canvas == null) {
                if (!bitmap!!.isMutable) {
                    bitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
                }
                _canvas = Canvas(bitmap!!)
            }

            return _canvas!!
        }

    fun isFogExists(tileX: Int, tileY: Int): Boolean {
        bitmap?.get(tileX, tileY)?.let { pixelColor ->
            val alpha = Color.alpha(pixelColor)
            return alpha > 128
        }
        return false
    }

    fun applyPosition(position: WorldPosition) {
        canvas.applyPosition(TileCoordinate(x, y, 15), position.xOfWold, position.yOfWorld, position.radius)
    }

    fun Canvas.applyPosition(tileCoordinate: TileCoordinate, xOfWold: Double, yOfWorld: Double, radius: Int) {
        val pixelCoordinate = TileCoordinateUtil.Companion.worldToPixel(xOfWold, yOfWorld, tileCoordinate.zoom)
        val pixelInTile = TileCoordinateUtil.Companion.pixelToPixelInTile(pixelCoordinate.first, pixelCoordinate.second, tileCoordinate)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        val centerX = pixelInTile.first.toFloat()
        val centerY = pixelInTile.second.toFloat()

        val radiusPx = TileCoordinateUtil.meterToPixel(
            radius.toDouble(),
            TileCoordinateUtil.yOfWorldToLat(yOfWorld),
            tileCoordinate.zoom
        ).toFloat()

        paint.shader = RadialGradient(
                centerX,
                centerY,
                radiusPx,
                intArrayOf(
                    FOG_COLOR,
                    FOG_COLOR,
                    Color.TRANSPARENT,
                ),
                floatArrayOf(
                    0f,
                    GRADIENT_RADIUS_RATE,
                    1f
                ),
                Shader.TileMode.CLAMP
            )

        drawCircle(
            pixelInTile.first.toFloat(),
            pixelInTile.second.toFloat(),
            (TileCoordinateUtil.Companion.meterToPixel(radius.toDouble(), TileCoordinateUtil.Companion.yOfWorldToLat(yOfWorld), tileCoordinate.zoom)).toFloat(),
            paint)
    }

    constructor(builder: Builder) {
        this.x = builder.x
        this.y = builder.y
        this.bitmap = builder.bitmap
    }

    constructor(tileCoordinate: TileCoordinate) {
        if (tileCoordinate.zoom != BASIC_ZOOM_LEVEL) {
            throw IllegalArgumentException("MapSection constructor only accepts zoom level $BASIC_ZOOM_LEVEL")
        }
        this.x = tileCoordinate.x
        this.y = tileCoordinate.y
    }

    class Builder {
        var x : Int = 0
        var y : Int = 0
        var bitmap : Bitmap? = null

        fun setXY(x : Int, y: Int) : Builder {
            this.x = x
            this.y = y
            return this
        }

        fun setBitmap(bitmap : Bitmap) : Builder {
            this.bitmap = bitmap
            return this
        }

        fun build() : MapSection {
            return MapSection(this)
        }
    }
}