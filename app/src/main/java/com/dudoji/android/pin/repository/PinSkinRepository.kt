package com.dudoji.android.pin.repository

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
import com.dudoji.android.pin.domain.PinSkin
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PinSkinRepository {
    var pinSkinList: Map<Long, PinSkin>? = null
    private val mutex = Mutex()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPinSkinById(id: Long): PinSkin? {
        mutex.withLock {
            if (pinSkinList == null) {
                loadPinSkins()
            }
        }
        return pinSkinList?.get(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPinSkinBitmapById(id: Long, context: Context): Bitmap? {
        val url = getPinSkinById(id)?.imageUrl ?: return null

        Log.d("PinSkinRepository", "Loading bitmap for pin skin ID: $id from URL: ${RetrofitClient.BASE_URL}/${url}")
        var bitmap: Bitmap
        mutex.withLock {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data("${RetrofitClient.BASE_URL}/${url}")
                .addHeader("Authorization", "Bearer ${RetrofitClient.TOKEN}")
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            bitmap = result.toBitmap()
        }
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPinSkinDrawableById(id: Long, context: Context): Drawable? {
        val url = getPinSkinById(id)?.imageUrl ?: return null

        Log.d("PinSkinRepository", "Loading bitmap for pin skin ID: $id from URL: ${RetrofitClient.BASE_URL}/${url}")

        mutex.withLock {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data("${RetrofitClient.BASE_URL}/${url}")
                .addHeader("Authorization", "Bearer ${RetrofitClient.TOKEN}")
                .build()

            return (loader.execute(request) as SuccessResult).drawable
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadPinSkins() {
        RetrofitClient.pinApiService.getPinSkins()
            .let { response ->
                if (response.isSuccessful) {
                    pinSkinList = response.body()?.associateBy({
                        it.skinId
                    }, {
                        PinSkin(
                            id = it.skinId,
                            name = it.name,
                            content = it.content,
                            imageUrl = it.imageUrl,
                            price = it.price,
                            isPurchased = it.isPurchased
                        )
                    })
                } else {
                    throw Exception("Failed to load pin skins: ${response.errorBody()?.string()}")
                }
            }
    }
}