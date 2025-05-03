package com.dudoji.android.map.domain.mapsection

import android.graphics.Bitmap

class DetailedMapSection : MapSection {
    private val bitmap : Bitmap
  
    constructor(builder: Builder) : super(builder) {
        bitmap = builder.bitmap!!
    }
    
    fun GetBitmap() : Bitmap {
        return bitmap
    }
}

