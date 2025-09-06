package com.dudoji.android.network

import android.content.Context
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

@Deprecated("Use CoilModule with Hilt instead", ReplaceWith("CoilModule"))
object Coil {

    lateinit var imageLoader: ImageLoader

    fun init(context: Context, client: OkHttpClient) {

        imageLoader = ImageLoader.Builder(context)
            .okHttpClient { client }
            .build()

        Coil.setImageLoader(imageLoader)
    }
}