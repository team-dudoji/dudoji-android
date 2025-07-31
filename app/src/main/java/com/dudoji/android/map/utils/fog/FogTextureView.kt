package com.dudoji.android.map.utils.fog

import android.content.Context
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import com.dudoji.android.config.FOG_PARTICLE_SPACING
import com.dudoji.android.map.domain.FogParticle

@Deprecated(message = "use Fog Particle Overlay View")
class FogTextureView(context: Context, attrs: AttributeSet? = null) : TextureView(context, attrs), TextureView.SurfaceTextureListener {

    private var eglDisplay: EGLDisplay? = null
    private var eglContext: EGLContext? = null
    private var eglSurface: EGLSurface? = null
    private var eglConfig: EGLConfig? = null

    private val renderer = FogRenderer(context)

    private var renderThread: Thread? = null
    @Volatile
    private var running = false

    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        initEGL(surface)
        renderer.onSurfaceCreated(null, null)
        renderer.onSurfaceChanged(null, width, height)
        Log.d("FogTextureView", "SurfaceTexture available: width=$width, height=$height")
        running = true
        renderThread = Thread(renderLoop, "FogRenderThread")
        renderThread?.start()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        setupParticles()
        renderer.onSurfaceChanged(null, width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        running = false
        try {
            renderThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        releaseEGL()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    private val renderLoop = Runnable {
        while (running) {
            if (eglDisplay == null) break

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            renderer.onDrawFrame(null)

            EGL14.eglSwapBuffers(eglDisplay, eglSurface)

            Log.d("FogTextureView", "Rendering frame... width: ${width}, height: ${height}")
            try {
                Thread.sleep(100) // 약 60fps
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun initEGL(surfaceTexture: SurfaceTexture) {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("Unable to get EGL14 display")
        }

        val version = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw RuntimeException("Unable to initialize EGL14")
        }

        val attribList = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_NONE
        )

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.size, numConfigs, 0)) {
            throw RuntimeException("Unable to choose EGL config")
        }
        eglConfig = configs[0]

        val attrib_list = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, attrib_list, 0)

        val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceTexture, surfaceAttribs, 0)

        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    private fun releaseEGL() {
        if (eglDisplay != null && eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(eglDisplay, eglSurface)
            EGL14.eglDestroyContext(eglDisplay, eglContext)
            EGL14.eglTerminate(eglDisplay)
        }
        eglDisplay = null
        eglSurface = null
        eglContext = null
    }

    // 파티클 설정 함수 (외부에서 호출 가능)
    fun setupParticles() {
        Log.d("FogTextureView", "Setting up particles width: ${width}, height: ${height}")

        if (width == 0 || height == 0) return
        val particleList = mutableListOf<FogParticle>()
        for (x in 0 until width step FOG_PARTICLE_SPACING) {
            for (y in 0 until height step FOG_PARTICLE_SPACING) {
                particleList.add(FogParticle(Point(x, y)))
            }
        }
        Log.d("FogTextureView", "Total particles created: ${particleList.size}")
        renderer.particles = particleList
    }

//    // 파티클 업데이트 함수 (외부에서 호출 가능)
//    fun updateParticles(mapSectionManager: DatabaseMapSectionManager, projection: Projection) {
//        renderer.updateParticles(mapSectionManager, projection)
//    }
}