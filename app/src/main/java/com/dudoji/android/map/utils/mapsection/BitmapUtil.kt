package com.dudoji.android.map.utils.mapsection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import java.io.ByteArrayOutputStream

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

        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }

        fun calculateTransparencyRatio(bitmap: Bitmap): Float {
            if (bitmap.config != Bitmap.Config.ARGB_8888) {
                throw IllegalArgumentException("Only ARGB_8888 bitmaps are supported.")
            }

            val width = bitmap.width
            val height = bitmap.height
            val totalPixels = width * height
            val pixels = IntArray(totalPixels)

            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            var transparentCount = 0
            for (pixel in pixels) {
                val alpha = (pixel shr 24) and 0xff
                if (alpha == 0) {
                    transparentCount++
                }
            }

            return transparentCount.toFloat() / totalPixels
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
}