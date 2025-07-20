package com.dudoji.android.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

object NetworkInitializer {
    @RequiresApi(Build.VERSION_CODES.O)
    fun init(context: Context) {
        // Initialize Retrofit client
        RetrofitClient.init(context)

        // Initialize Coil image loader
        CoilConfigurer.initCoil(context)
    }
}