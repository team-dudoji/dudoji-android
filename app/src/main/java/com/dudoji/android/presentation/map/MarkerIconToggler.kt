package com.dudoji.android.presentation.map

import com.dudoji.android.R
import com.dudoji.android.config.SPEED_THRESHOLD
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarkerIconToggler(private val marker: Marker) {

    private var useAltIcon = false
    private var blinkJob: Job? = null
    private var speed: Float = 0f
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun setSpeed(newSpeed: Float) {
        speed = newSpeed
        if (speed > SPEED_THRESHOLD) {
            startBlinkingIfNeeded()
        } else {
            stopBlinking()
        }
    }

    private fun startBlinkingIfNeeded() {
        if (blinkJob?.isActive == true) return

        blinkJob = scope.launch {
            while (speed > SPEED_THRESHOLD) {
                val interval = (1000 / (1 + speed)).toLong().coerceIn(100L, 1000L)

                val iconRes = if (useAltIcon) R.drawable.marker_alt else R.drawable.marker_main
                marker.setIcon(BitmapDescriptorFactory.fromResource(iconRes))
                useAltIcon = !useAltIcon

                delay(interval)
            }
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_main))
            useAltIcon = false
        }
    }

    private fun stopBlinking() {
        blinkJob?.cancel()
        blinkJob = null
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_main))
        useAltIcon = false
    }

    fun clear() {
        scope.cancel()
    }
}