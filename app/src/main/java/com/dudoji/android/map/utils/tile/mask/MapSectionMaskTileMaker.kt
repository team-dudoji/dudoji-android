package com.dudoji.android.map.utils.tile.mask

import android.graphics.Bitmap
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.domain.TileCoordinate


class MapSectionMaskTileMaker(private val mapSectionManager: MapSectionManager): IMaskTileMaker {
    override suspend fun createMaskTile(x : Int, y : Int, zoom : Int): Bitmap {
        return mapSectionManager.getBitmap(TileCoordinate(x, y, zoom))!!
    }
}