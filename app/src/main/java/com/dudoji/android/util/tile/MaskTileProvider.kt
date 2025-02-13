package com.dudoji.android.util.tile


import android.graphics.Bitmap
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

const val TILE_SIZE = 256

// Mask Tile Provider for Map Activity
class MaskTileProvider(private val maskTileMaker: IMaskTileMaker) : TileProvider {

    override fun getTile(x : Int, y : Int, zoom : Int) : Tile {
        val bitmap : Bitmap = runBlocking { maskTileMaker.createMaskTile(x, y, zoom) }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Tile(TILE_SIZE, TILE_SIZE, byteArray)
    }
}