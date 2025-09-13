package com.dudoji.android.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dudoji.android.data.network.NetworkModule
import com.dudoji.android.data.remote.api.PinApiService
import com.dudoji.android.domain.model.PinSkin
import com.dudoji.android.domain.repository.PinSkinRepository
import com.dudoji.android.pin.api.dto.PinSkinDto
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class PinSkinRepositoryImpl @Inject constructor(
    val pinApiService: PinApiService,
    val imageLoader: ImageLoader
): PinSkinRepository {

    companion object {
        val pinSkinCache = mutableMapOf<Long, PinSkin>()
    }

    override suspend fun getPinSkinById(id: Long): PinSkin? {
        getPinSkins()
        if (pinSkinCache.containsKey(id)) {
            return pinSkinCache[id]
        }
        return null

//        val response = pinApiService.getPinSkin(id)
//        if (!response.isSuccessful) {
//            Log.e("PinSkinRepository", "Failed to load pin skin with ID $id: ${response.errorBody()?.string()}")
//            return null
//        }
//        val pinSkinDto = response.body() ?: return null
//        return pinSkinDto.toDomain()
    }

    override suspend fun getPinSkinBitmapById(id: Long, context: Context): Bitmap? {
        val drawable: Drawable = getPinSkinDrawableById(id, context) ?: return null
        return drawable.toBitmap()
    }

    override suspend fun getPinSkinDrawableById(id: Long, context: Context): Drawable? {
        val url = getPinSkinById(id)?.imageUrl ?: return null

        val loader = imageLoader
        val request = ImageRequest.Builder(context)
            .data("${NetworkModule.BASE_URL}/${url}")
            .build()

        val result = loader.execute(request)
        return if (result is SuccessResult) {
            result.drawable
        } else {
            Log.e("PinSkinRepository", "Failed to load image for pin skin ID: $id")
            null
        }
    }

    override suspend fun getPinSkins(): List<PinSkin> {
        val response = pinApiService.getPinSkins()

        if (!response.isSuccessful) {
            Log.e("PinSkinRepository", "Failed to load pin skins: ${response.errorBody()?.string()}")
        }

        response.body()
            ?.stream()
            ?.forEach {
                dto ->
                if (pinSkinCache.containsKey(dto.skinId)) return@forEach
                pinSkinCache[dto.skinId] = dto.toDomain()
            }

        return response.body()
            ?.map(PinSkinDto::toDomain)
            ?.toList() ?: emptyList()
    }
}