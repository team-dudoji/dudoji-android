package com.dudoji.android.map.domain

import android.graphics.Point
import android.util.Log
import com.dudoji.android.config.FOG_INVALIDATION_INTERVAL

data class FogParticle(
    val screenPoint: Point,
    val type: Int = 0,
    var alpha: Int = 255,
    var visible: Boolean = true
) {
    companion object {
        const val DELTA_ALPHA_PER_MS = 4
    }

    fun updateAlpha() {
        if (visible && alpha != 255) {
            alpha += (FOG_INVALIDATION_INTERVAL * DELTA_ALPHA_PER_MS).toInt()
            if (alpha > 255) {
                alpha = 255
            }
        } else if (!visible && alpha != 0) {
            alpha -= (FOG_INVALIDATION_INTERVAL * DELTA_ALPHA_PER_MS).toInt()
            if (alpha < 0) {
                alpha = 0
            }
        }
        Log.d("FogParticle", "updateAlpha: type=$type, alpha=$alpha, visible=$visible")
    }
}