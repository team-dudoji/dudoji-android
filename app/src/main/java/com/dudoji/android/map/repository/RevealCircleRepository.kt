package com.dudoji.android.map.repository

import android.location.Location
import android.util.Log
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.map.domain.RevealCircle
import com.dudoji.android.map.utils.location.IRevealCircleListener
import com.dudoji.android.util.listener.ListenerCaller

// This repository is responsible for managing the reveal circle data.
object RevealCircleRepository {
    private const val TAG = "RevealCircleRepository_Database"

    val revealCircleListenerCaller: ListenerCaller<IRevealCircleListener, RevealCircle> = ListenerCaller()

    // Add a listener to the listener caller
    fun addLocation(location: Location) {
        Log.d(TAG, "addLocation: $location")
        val revealCircle = RevealCircle(location.latitude, location.longitude, REVEAL_CIRCLE_RADIUS_BY_WALK)
        synchronized(this) {
            revealCircleListenerCaller.callListeners(revealCircle)
        }
    }
}
