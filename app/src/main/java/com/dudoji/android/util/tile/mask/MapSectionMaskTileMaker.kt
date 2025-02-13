package com.dudoji.android.util.tile.mask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.dudoji.android.model.mapsection.MapSectionProcessor
import com.dudoji.android.util.tile.TILE_SIZE
import com.dudoji.android.util.tile.TilePositionUtil
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val TILE_PIXEL_SIZE = 2

class MapSectionMaskTileMaker(private val mapSectionProcessor: MapSectionProcessor): IMaskTileMaker {

    override suspend fun createMaskTile(x : Int, y : Int, zoom : Int): Bitmap {
        val minLatLng = TilePositionUtil.tilePositionToLatLng(x, y, zoom)
        val tileSize = TilePositionUtil.getTileSize(zoom)
        val fragmentSize = tileSize / TILE_SIZE

        val bitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        var paint = Paint()

        // apply black color with 50% transparency
        paint.setColor(Color.RED)
        canvas.drawRect(0F, 0F, TILE_SIZE.toFloat(), TILE_SIZE.toFloat(), paint)
        var count = 0
        val mutex = Mutex()
        val deferredList = mutableListOf<Deferred<Unit>>()

        coroutineScope {
            for (i in 0 until (TILE_SIZE-10)/TILE_PIXEL_SIZE) {
                for (j in 0 until (TILE_SIZE-10)/TILE_PIXEL_SIZE) {
                    deferredList.add(
                        async {
                            val lat = minLatLng.first + i * TILE_PIXEL_SIZE * fragmentSize
                            val lng = minLatLng.second + j * TILE_PIXEL_SIZE * fragmentSize
                            val bitValue = !mapSectionProcessor.getBit(lat, lng, fragmentSize * TILE_PIXEL_SIZE)

                            val color = if (bitValue) Color.BLACK else Color.YELLOW//Color.argb(128, 0, 0, 0)

                            mutex.withLock {
                                count += 1
                                for (k in 0 until TILE_PIXEL_SIZE) {
                                    for (l in 0 until TILE_PIXEL_SIZE) {
                                        bitmap.setPixel(j * TILE_PIXEL_SIZE + k, i * TILE_PIXEL_SIZE + l, color)
                                    }
                                }
                            }
                        }
                    )
                }
            }
            deferredList.awaitAll()
        }
        return bitmap
    }
}