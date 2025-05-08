package com.dudoji.android.map.domain.mapsection

import android.graphics.Bitmap
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.config.FOG_COLOR
import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.utils.mapsection.BitmapUtil
import com.dudoji.android.map.utils.tile.TILE_SIZE

class DetailedMapSection : MapSection {
    private var bitmap : Bitmap
  
    constructor(builder: Builder) : super(builder) {
        bitmap = builder.bitmap!!
    }

    constructor(tileCoordinate: TileCoordinate) : super(tileCoordinate) {
        if (tileCoordinate.zoom != BASIC_ZOOM_LEVEL) {
            throw IllegalArgumentException("MapSection constructor only accepts zoom level $BASIC_ZOOM_LEVEL")
        }

        bitmap = BitmapUtil.createBitmapWithColor(TILE_SIZE, TILE_SIZE, FOG_COLOR)
    }
    
    fun getBitmap() : Bitmap {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }
}

