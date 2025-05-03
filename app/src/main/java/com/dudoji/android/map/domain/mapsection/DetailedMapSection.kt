package com.dudoji.android.map.domain.mapsection

import android.graphics.Bitmap

class DetailedMapSection : MapSection {
    private var bitmap : Bitmap
  
    constructor(builder: Builder) : super(builder) {
        bitmap = builder.bitmap!!
    }
    
    fun getBitmap() : Bitmap {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }
}

