package com.dudoji.android.util.tile.mask

import android.graphics.Bitmap
import com.dudoji.android.model.MapSectionManager
import com.dudoji.android.model.TileCoordinate


class MapSectionMaskTileMaker(private val mapSectionManager: MapSectionManager): IMaskTileMaker {
    override suspend fun createMaskTile(x : Int, y : Int, zoom : Int): Bitmap {
        return mapSectionManager.getBitmap(TileCoordinate(x, y, zoom))!!
    }
}