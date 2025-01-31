package com.dudoji.android.model.mapsection

open class MapSection {
    val x : Int
    val y : Int


    constructor(builder: Builder) {
        this.x = builder.x
        this.y = builder.y
    }

    class Builder {
        var x : Int = 0
        var y : Int = 0
        var bitmap : Bitmap? = null

        fun setX(x : Int, y: Int) : Builder {
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