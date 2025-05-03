package com.dudoji.android.map.domain.mapsection

import android.graphics.Bitmap
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.map.domain.TileCoordinate

open class MapSection {
    val x : Int
    val y : Int

    constructor(builder: Builder) {
        this.x = builder.x
        this.y = builder.y
    }

    constructor(tileCoordinate: TileCoordinate) {
        if (tileCoordinate.zoom != BASIC_ZOOM_LEVEL) {
            throw IllegalArgumentException("MapSection constructor only accepts zoom level $BASIC_ZOOM_LEVEL")
        }
        this.x = tileCoordinate.x
        this.y = tileCoordinate.y
    }


    constructor(detailedMapSection: DetailedMapSection) {
        this.x = detailedMapSection.x
        this.y = detailedMapSection.y
    }

    class Builder {
        var x : Int = 0
        var y : Int = 0
        var bitmap : Bitmap? = null

        fun setXY(x : Int, y: Int) : Builder {
            this.x = x
            this.y = y
            return this
        }

        fun setBitmap(bitmap : Bitmap) : Builder {
            this.bitmap = bitmap
            return this
        }

        fun build() : MapSection {
            if (bitmap == null) {
                return MapSection(this)
            } else {
                return DetailedMapSection(this)
            }
        }
    }
}