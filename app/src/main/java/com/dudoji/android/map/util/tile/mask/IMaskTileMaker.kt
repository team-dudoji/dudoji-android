package com.dudoji.android.map.util.tile.mask

import android.graphics.Bitmap

// for MaskTile Provider
interface IMaskTileMaker {
    suspend fun createMaskTile(x : Int, y : Int, zoom : Int): Bitmap
}