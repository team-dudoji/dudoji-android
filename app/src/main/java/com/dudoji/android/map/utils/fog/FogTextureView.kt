package com.dudoji.android.map.utils.fog

import EGLHelper
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import android.view.View
import com.dudoji.android.R
import com.dudoji.android.config.FOG_CHECK_INTERVAL
import com.dudoji.android.config.FOG_INVALIDATION_INTERVAL
import com.dudoji.android.config.FOG_PARTICLE_SIZE
import com.dudoji.android.config.FOG_PARTICLE_SPACING
import com.dudoji.android.map.domain.FogParticle
import com.dudoji.android.map.manager.DatabaseMapSectionManager
import com.google.android.gms.maps.GoogleMap
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.opengles.GL10

class FogTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    private lateinit var googleMap: GoogleMap
    private var particles = listOf<FogParticle>()
    private var isInitialized = false
    private var renderThread: RenderThread? = null

    private var lastUpdateTime = 0L

    init {
        surfaceTextureListener = this
        isOpaque = false // Make the TextureView transparent
    }

    fun setGoogleMap(map: GoogleMap) {
        this.googleMap = map
    }

    fun onCameraMoved(mapSectionManager: DatabaseMapSectionManager) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime >= FOG_CHECK_INTERVAL) {
            updateParticles(mapSectionManager)
            lastUpdateTime = currentTime
        }
    }

    fun updateParticles(mapSectionManager: DatabaseMapSectionManager) {
        synchronized(particles) {
            particles.forEach { particle ->
                val latLng = googleMap.projection.fromScreenLocation(particle.screenPoint)
                particle.visible = mapSectionManager.isFogExists(latLng.latitude, latLng.longitude)
            }
            renderThread?.setParticles(particles)
        }
    }

    private fun setupParticles() {
        val particleList = mutableListOf<FogParticle>()

        Log.d("FogTextureView", "Setting up particles... Width: $width, Height: $height")
        for (x in 0 until width + FOG_PARTICLE_SPACING step FOG_PARTICLE_SPACING) {
            for (y in 0 until height + FOG_PARTICLE_SPACING step FOG_PARTICLE_SPACING) {
                val screenPoint = Point(x, y)
                val particle = FogParticle(screenPoint, type = 0, visible = true)
                particleList.add(particle)
            }
        }

        synchronized(particles) {
            particles = particleList
            renderThread?.setParticles(particles)
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        isOpaque = false // Ensure TextureView is transparent
        setLayerType(View.LAYER_TYPE_HARDWARE, null) // Explicitly set hardware layer type
        renderThread = RenderThread(context, surface, width, height).apply {
            start()
        }
        setupParticles()
        isInitialized = true
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        renderThread?.updateViewport(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        renderThread?.requestExitAndWait()
        renderThread = null
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // Not used for continuous rendering
    }

    private class RenderThread(
        private val context: Context,
        private val surfaceTexture: SurfaceTexture,
        private var width: Int,
        private var height: Int
    ) : Thread() {

        private var eglHelper: EGLHelper? = null
        private var renderer: FogParticleRenderer? = null
        private val running = AtomicBoolean(true)

        override fun run() {
            eglHelper = EGLHelper().apply {
                createGLContext(surfaceTexture)
            }
            renderer = FogParticleRenderer(context, width, height).apply {
                onSurfaceCreated(null, null)
                onSurfaceChanged(null, width, height)
            }

            while (running.get()) {
                eglHelper?.makeCurrent()
                Log.d("RenderThread", "Clearing GL buffer.")
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) // Clear the buffer with transparent color

                renderer?.onDrawFrame() // Call the renderer's draw method

                eglHelper?.swapBuffers()

                try {
                    sleep(FOG_INVALIDATION_INTERVAL.toLong()) // Control rendering speed
                } catch (e: InterruptedException) {
                    currentThread().interrupt()
                }
            }
            eglHelper?.release()
        }

        fun requestExitAndWait() {
            running.set(false)
            try {
                join()
            } catch (e: InterruptedException) {
                currentThread().interrupt()
            }
        }

        fun setParticles(newParticles: List<FogParticle>) {
            renderer?.setParticles(newParticles)
        }

        fun updateViewport(newWidth: Int, newHeight: Int) {
            width = newWidth
            height = newHeight
            renderer?.updateViewport(newWidth, newHeight)
        }
    }

    private class FogParticleRenderer(
        private val context: Context,
        private var width: Int,
        private var height: Int
    ) {

        private var particles = listOf<FogParticle>()
        private var textureId = -1
        private var programHandle = -1
        private var positionHandle = -1
        private var alphaHandle = -1
        private var mvpMatrixHandle = -1
        private var textureHandle = -1
        private var pointSizeHandle = -1

        private val mvpMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)

        private lateinit var vertexBuffer: FloatBuffer
        private lateinit var alphaBuffer: FloatBuffer

        fun setParticles(newParticles: List<FogParticle>) {
            synchronized(particles) {
                particles = newParticles
                prepareBuffers()
            }
        }

        fun updateViewport(newWidth: Int, newHeight: Int) {
            width = newWidth
            height = newHeight
            Matrix.orthoM(projectionMatrix, 0, 0f, width.toFloat(), height.toFloat(), 0f, -1f, 1f)
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        }

        fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            Log.d("FogTextureView", "onSurfaceCreated: Setting clear color to transparent black (0,0,0,0)")
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

            programHandle = GLES20.glCreateProgram()
            GLES20.glAttachShader(programHandle, vertexShader)
            GLES20.glAttachShader(programHandle, fragmentShader)
            GLES20.glLinkProgram(programHandle)

            positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
            alphaHandle = GLES20.glGetAttribLocation(programHandle, "a_Alpha")
            mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
            textureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
            pointSizeHandle = GLES20.glGetUniformLocation(programHandle, "u_PointSize")

            loadTexture()
            updateViewport(width, height)
        }

        fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            updateViewport(width, height)
        }

        fun onDrawFrame() {
            synchronized(particles) {
                Log.d("FogTextureView", "Drawing frame with ${particles.size} particles")

                // Update particle alphas
                particles.forEach { it.updateAlpha() }
                prepareBuffers() // Re-prepare buffers with updated alpha values

                GLES20.glUseProgram(programHandle)

                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
                GLES20.glUniform1i(textureHandle, 0)

                GLES20.glUniform1f(pointSizeHandle, FOG_PARTICLE_SIZE.toFloat())

                GLES20.glEnableVertexAttribArray(positionHandle)
                GLES20.glVertexAttribPointer(
                    positionHandle, 2, GLES20.GL_FLOAT, false,
                    0, vertexBuffer
                )

                GLES20.glEnableVertexAttribArray(alphaHandle)
                GLES20.glVertexAttribPointer(
                    alphaHandle, 1, GLES20.GL_FLOAT, false,
                    0, alphaBuffer
                )

                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, particles.size)

                GLES20.glDisableVertexAttribArray(positionHandle)
                GLES20.glDisableVertexAttribArray(alphaHandle)
            }
        }

        private fun prepareBuffers() {
            val vertices = FloatArray(particles.size * 2)
            val alphas = FloatArray(particles.size)

            particles.forEachIndexed { index, particle ->
                vertices[index * 2] = particle.screenPoint.x.toFloat()
                vertices[index * 2 + 1] = particle.screenPoint.y.toFloat()

                alphas[index] = particle.alpha / 255f
            }

            vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(vertices)
                    position(0)
                }

            alphaBuffer = ByteBuffer.allocateDirect(alphas.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(alphas)
                    position(0)
                }
        }

        private fun loadTexture() {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            textureId = textures[0]

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

            val bitmap = context.assets.open("map/fog_particle.png").use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        companion object {
            private const val VERTEX_SHADER = """
                uniform mat4 u_MVPMatrix;
                uniform float u_PointSize;
                attribute vec2 a_Position;
                attribute float a_Alpha;
                varying float v_Alpha;
                void main() {
                    gl_Position = u_MVPMatrix * vec4(a_Position, 0.0, 1.0);
                    gl_PointSize = u_PointSize;
                    v_Alpha = a_Alpha;
                }
            """

            private const val FRAGMENT_SHADER = """
                precision mediump float;
                uniform sampler2D u_Texture;
                varying float v_Alpha;
                void main() {
                    vec4 texColor = texture2D(u_Texture, gl_PointCoord);
                    gl_FragColor = vec4(texColor.rgb, texColor.a * v_Alpha);
                }
            """
        }
    }
}