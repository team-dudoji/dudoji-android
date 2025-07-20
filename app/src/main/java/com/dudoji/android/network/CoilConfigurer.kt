package com.dudoji.android.network

import android.content.Context
import coil.Coil
import coil.ImageLoader
import com.dudoji.android.login.util.getEncryptedPrefs

import okhttp3.OkHttpClient

object CoilConfigurer {

    fun initCoil(context: Context) {

        val prefs = getEncryptedPrefs(context)
        val token = prefs.getString("jwt", null)

        val imageLoader = ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .build()

        Coil.setImageLoader(imageLoader)
    }
}