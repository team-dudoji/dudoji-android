package com.dudoji.android.data.datasource.location

import android.location.Location

interface LocationService {
    companion object{
        const val LOCATION_CALLBACK_INTERVAL = 1000L
    }

    fun setLocationCallback(callback: (Location)->Unit)
}