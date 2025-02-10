package com.dudoji.android.util.tile.mask

import android.graphics.Bitmap
import android.graphics.Color
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

class MapSectionMaskTileMaker(private val mapSectionProcessor: MapSectionProcessor): IMaskTileMaker {

    override suspend fun createMaskTile(x : Int, y : Int, zoom : Int): Bitmap {
        val minLatLng = TilePositionUtil.tilePositionToLatLng(x, y, zoom)
        val tileSize = TilePositionUtil.getTileSize(zoom)
        val fragmentSize = tileSize / TILE_SIZE

        val bitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)
        var count = 0
        val mutex = Mutex()
        val deferredList = mutableListOf<Deferred<Unit>>()

        coroutineScope {
            for (i in 0 until TILE_SIZE) {
                for (j in 0 until TILE_SIZE) {
                    deferredList.add(
                        async {
                            val lat = minLatLng.first + i * fragmentSize
                            val lng = minLatLng.second + j * fragmentSize
                            val bitValue = !mapSectionProcessor.getBit(lat, lng, fragmentSize)

                            val color = if (bitValue) Color.BLACK else Color.WHITE

                            mutex.withLock {
                                count += 1
                                bitmap.setPixel(j, i, color)
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