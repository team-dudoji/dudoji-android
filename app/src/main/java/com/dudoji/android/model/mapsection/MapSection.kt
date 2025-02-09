package com.dudoji.android.model.mapsection

open class MapSection {
    val x : Int
    val y : Int

    companion object {
        const val MAPSECTION_LATLNG_SIZE = 0.0115
        const val BASE_LAT = 35.230853
        const val BASE_LNG = 129.082255
    }

    constructor(builder: Builder) {
        this.x = builder.x
        this.y = builder.y
    }

    open fun getExploredRate() : Float {
        return 1.0f
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