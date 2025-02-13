package com.dudoji.android.util.mapsection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class BitmapUtil {
    companion object {
        // Crop a bitmap in grid(numOfTile x numOfTile) at (xOfTile, yOfTile)
        fun cropBitmapInGrid(originalBitmap: Bitmap, originalTileSize: Int, numOfTile: Int, xOfTile:Int, yOfTile:Int):Bitmap {
            val croppedTileSize = originalTileSize / numOfTile
            val bitmap = Bitmap.createBitmap(originalTileSize, originalTileSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            canvas.drawBitmap(
                originalBitmap,
                Rect(xOfTile * croppedTileSize, yOfTile * croppedTileSize, (xOfTile + 1) * croppedTileSize, (yOfTile + 1) * croppedTileSize),
                Rect(0, 0, originalTileSize, originalTileSize),
                null
            )
            return bitmap
        }

        // Combine a subBitmap to baseBitmap in grid(numOfTile x numOfTile) at (xOfTile, yOfTile)
        fun Canvas.combineBitmapInGrid(subBitmap: Bitmap,  numOfTile: Int, xOfTile: Int, yOfTile: Int) {
            val fragmentBitmapWidth = width / numOfTile
            val fragmentBitmapHeight = height / numOfTile
            drawBitmap(
                subBitmap,
                Rect(0, 0, subBitmap.width, subBitmap.height),
                Rect(xOfTile * fragmentBitmapWidth, yOfTile * fragmentBitmapHeight, (xOfTile + 1) * fragmentBitmapWidth, (yOfTile + 1) * fragmentBitmapHeight),
                null
            )
        }
    }
}