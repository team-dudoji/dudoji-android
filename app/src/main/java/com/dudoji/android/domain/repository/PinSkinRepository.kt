package com.dudoji.android.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.dudoji.android.domain.model.PinSkin

interface PinSkinRepository {
    suspend fun getPinSkinById(id: Long): PinSkin?
    suspend fun getPinSkinBitmapById(id: Long, context: Context): Bitmap?
    suspend fun getPinSkinDrawableById(id: Long, context: Context): Drawable?
    suspend fun getPinSkins(): List<PinSkin>
}