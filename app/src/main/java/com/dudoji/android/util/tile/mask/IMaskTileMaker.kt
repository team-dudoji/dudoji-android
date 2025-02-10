package com.dudoji.android.util.tile.mask

import android.graphics.Bitmap

// for MaskTile Provider
interface IMaskTileMaker {
    fun createMaskTile(): Bitmap
}