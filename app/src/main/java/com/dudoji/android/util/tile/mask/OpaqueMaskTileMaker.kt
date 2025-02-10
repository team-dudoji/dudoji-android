package com.dudoji.android.util.tile.mask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.dudoji.android.util.tile.TILE_SIZE

// for Test MaskTileMaker (50% transparency mask)
class OpaqueMaskTileMaker : IMaskTileMaker {
    override fun createMaskTile(x : Int, y : Int, zoom : Int) : Bitmap {
        var bitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        var paint = Paint()

        // apply black color with 50% transparency
        paint.setColor(Color.argb(128, 0, 0, 0))
        canvas.drawRect(0F, 0F, TILE_SIZE.toFloat(), TILE_SIZE.toFloat(), paint)

        return bitmap;
    }
}