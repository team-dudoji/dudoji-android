package com.dudoji.android.map.domain.mapsection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.config.FOG_COLOR
import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.domain.WorldPosition
import com.dudoji.android.map.utils.tile.TILE_SIZE
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

    fun applyPosition(position: WorldPosition) {
        canvas.applyPosition(TileCoordinate(x, y, 15), position.xOfWold, position.yOfWorld, position.radius)
    }

    fun Canvas.applyPosition(tileCoordinate: TileCoordinate, xOfWold: Double, yOfWorld: Double, radius: Int) {
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