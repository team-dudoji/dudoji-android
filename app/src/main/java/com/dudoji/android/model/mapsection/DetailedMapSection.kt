package com.dudoji.android.model.mapsection

import android.graphics.Bitmap

class DetailedMapSection : MapSection {
    private val bitmap : Bitmap
    constructor(builder: Builder) : super(builder) {
        bitmap = builder.bitmap!!
    }

    fun GetBitmap() : Bitmap {
        return bitmap
    }

    @Deprecated(COORDINATE_CHANGE_WARNING_TEXT)
    override fun getExploredRate() : Float {
//        return bitmap.getFilledRate()
        return 0.0f
    }
}
