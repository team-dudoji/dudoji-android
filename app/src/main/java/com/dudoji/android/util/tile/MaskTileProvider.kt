package com.dudoji.android.util.tile


import android.graphics.Bitmap
import com.dudoji.android.util.tile.mask.IMaskTileMaker
import com.google.android.gms.maps.model.*
import java.io.ByteArrayOutputStream

const val TILE_SIZE = 256

// Mask Tile Provider for Map Activity
class MaskTileProvider : TileProvider {
    private var maskTileMaker : IMaskTileMaker

    constructor(maskTileMaker : IMaskTileMaker) {
        this.maskTileMaker = maskTileMaker
    }

    fun setMaskTileMaker(maskTileMaker : IMaskTileMaker) {
        this.maskTileMaker = maskTileMaker
    }

    override fun getTile(x : Int, y : Int, zoom : Int) : Tile {
        var bitmap : Bitmap = maskTileMaker.createMaskTile()
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return Tile(TILE_SIZE, TILE_SIZE, stream.toByteArray())
    }
}