package com.dudoji.android.model.mapsection

class DetailedMapSection : MapSection {
    private val bitmap : Bitmap
    constructor(builder: Builder) : super(builder) {
        bitmap = builder.bitmap!!
    }
}