package com.dudoji.android.map.utils.fog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.dudoji.android.R
import com.dudoji.android.config.FOG_CHECK_INTERVAL
import com.dudoji.android.config.FOG_INVALIDATION_INTERVAL
import com.dudoji.android.config.FOG_PARTICLE_SPACING
import com.dudoji.android.map.domain.FogParticle
import com.dudoji.android.map.manager.DatabaseMapSectionManager
import com.google.android.gms.maps.GoogleMap

class FogParticleOverlayView(
    context: Context,
    attrs:AttributeSet
) : View(context, attrs) {

    lateinit var googleMap:
            GoogleMap
    var particles = listOf<FogParticle>()
    var isInitialized = false

    private val invalidateHandler = Handler(Looper.getMainLooper())
    private val invalidateRunnable = object : Runnable {
        override fun run() {
            invalidate()
            invalidateHandler.postDelayed(this, FOG_INVALIDATION_INTERVAL.toLong())
        }
    }

    private val bitmapCache = mutableMapOf<Int, Bitmap>()

    init {
        startInvalidateLoop()
    }

    private fun startInvalidateLoop() {
        invalidateHandler.post(invalidateRunnable)
    }

    private fun stopInvalidateLoop() {
        invalidateHandler.removeCallbacks(invalidateRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopInvalidateLoop()
    }

    override fun onDraw(canvas:Canvas) {
        super.onDraw(canvas)
        if (!::googleMap.isInitialized) return
        if (!isInitialized) {
            init()
        }

        particles.forEach { particle ->
            particle.updateAlpha()
            if (particle.alpha == 0) return@forEach
            val bitmap = getBitmapForType(particle.type)
            val bitmapHalfWidth = bitmap.width / 2f
            val bitmapHalfHeight = bitmap.height / 2f
            val paint = Paint().apply {
                alpha = particle.alpha
            }

            canvas.drawBitmap(
                bitmap,
                particle.screenPoint.x - bitmapHalfWidth,
                particle.screenPoint.y - bitmapHalfHeight,
                paint
            )

        }
    }

    var lastUpdateTime = 0L
    fun onCameraMoved(mapSectionManager: DatabaseMapSectionManager) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime >= FOG_CHECK_INTERVAL) {
            updateParticles(mapSectionManager)
            lastUpdateTime = currentTime
        }
    }

    fun updateParticles(mapSectionManager: DatabaseMapSectionManager) {
        particles.forEach { particle ->
            val latLng = googleMap.projection.fromScreenLocation(particle.screenPoint)
            particle.visible = mapSectionManager.isFogExists(latLng.latitude, latLng.longitude)
        }
        invalidate()
    }

    private fun init() {
        setupParticles()
        isInitialized = true
    }

    private fun setupParticles() {

        val particleList = mutableListOf<FogParticle>()

        for (x in 0 until width + FOG_PARTICLE_SPACING step FOG_PARTICLE_SPACING) {
            for (y in 0 until height + FOG_PARTICLE_SPACING step FOG_PARTICLE_SPACING) {
                val screenPoint = Point(x, y)
                val particle = FogParticle(screenPoint, type = 0, visible = true)
                particleList.add(particle)
            }
        }

        particles = particleList
        invalidate()
    }


    private fun getBitmapForType(type: Int):Bitmap {
        if (bitmapCache.containsKey(type)) {
            return bitmapCache[type]!!
        }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.fog_particle)
        bitmapCache[type] = bitmap
        return bitmap
    }
}
