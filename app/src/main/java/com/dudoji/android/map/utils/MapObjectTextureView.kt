package com.dudoji.android.map.utils

import EGLHelper
import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicBoolean

// This class now holds screen coordinates, not LatLng
class MapObject(
    val screenX: Float,
    val screenY: Float,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val width: Int? = null,
    val height: Int? = null,
    val bitmap: Bitmap
)

class MapObjectTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    private var renderThread: RenderThread? = null

    init {
        surfaceTextureListener = this
        isOpaque = false
    }

    // This method now takes objects with pre-calculated screen coordinates
    fun setMapObjects(objects: List<MapObject>) {
        renderThread?.setMapObjects(objects)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        isOpaque = false
        setLayerType(LAYER_TYPE_HARDWARE, null)
        renderThread = RenderThread(context, surface, width, height).apply {
            start()
        }
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
        // Not used
    }

    private class RenderThread(
        private val context: Context,
        private val surfaceTexture: SurfaceTexture,
        private var width: Int,
        private var height: Int
    ) : Thread() {

        private var eglHelper: EGLHelper? = null
        private var renderer: MapObjectRenderer? = null
        private val running = AtomicBoolean(true)
        private val objectLock = Any()
        private var pendingObjects: List<MapObject>? = null

        override fun run() {
            try {
                eglHelper = EGLHelper().apply {
                    createGLContext(surfaceTexture)
                }
                renderer = MapObjectRenderer(width, height)
                renderer?.onSurfaceCreated()

                while (running.get()) {
                    synchronized(objectLock) {
                        pendingObjects?.let {
                            renderer?.setMapObjects(it)
                            pendingObjects = null
                        }
                    }

                    eglHelper?.makeCurrent()
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                    renderer?.onDrawFrame()
                    eglHelper?.swapBuffers()

                    try {
                        sleep(16)
                    } catch (e: InterruptedException) {
                        currentThread().interrupt()
                    }
                }
            } catch (e: Exception) {
                Log.e("RenderThread", "Exception in RenderThread", e)
            } finally {
                renderer?.release()
                eglHelper?.release()
            }
        }

        fun requestExitAndWait() {
            running.set(false)
            try {
                join()
            } catch (e: InterruptedException) {
                currentThread().interrupt()
            }
        }

        fun setMapObjects(newObjects: List<MapObject>) {
            synchronized(objectLock) {
                pendingObjects = newObjects
            }
        }

        fun updateViewport(newWidth: Int, newHeight: Int) {
            width = newWidth
            height = newHeight
            renderer?.updateViewport(newWidth, newHeight)
        }
    }

    private class MapObjectRenderer(
        private var width: Int,
        private var height: Int
    ) {

        private var mapObjects = listOf<MapObject>()
        private val textureMap = mutableMapOf<Bitmap, Int>()
        private var programHandle = -1
        private var positionHandle = -1
        private var texCoordHandle = -1
        private var mvpMatrixHandle = -1
        private var textureHandle = -1

        private val mvpMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)

        private val quadVertices: FloatBuffer
        private val quadTexCoords: FloatBuffer

        init {
            val vertices = floatArrayOf(
                -0.5f, -0.5f,
                 0.5f, -0.5f,
                -0.5f,  0.5f,
                 0.5f,  0.5f
            )
            quadVertices = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(vertices)
                position(0)
            }

            val texCoords = floatArrayOf(
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
            )
            quadTexCoords = ByteBuffer.allocateDirect(texCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(texCoords)
                position(0)
            }
        }

        fun setMapObjects(newObjects: List<MapObject>) {
            synchronized(this) {
                val newBitmaps = newObjects.map { it.bitmap }.toSet()
                val oldBitmaps = textureMap.keys.toSet()
                val bitmapsToDelete = oldBitmaps - newBitmaps
                
                val texturesToDelete = bitmapsToDelete.mapNotNull { textureMap[it] }.toIntArray()
                if (texturesToDelete.isNotEmpty()) {
                    GLES20.glDeleteTextures(texturesToDelete.size, texturesToDelete, 0)
                    bitmapsToDelete.forEach { textureMap.remove(it) }
                }

                newObjects.forEach { obj ->
                    if (!textureMap.containsKey(obj.bitmap)) {
                        loadTexture(obj.bitmap)
                    }
                }
                this.mapObjects = newObjects
            }
        }

        fun updateViewport(newWidth: Int, newHeight: Int) {
            width = newWidth
            height = newHeight
            GLES20.glViewport(0, 0, width, height)
            // Invert the Y-axis in the projection matrix
            Matrix.orthoM(projectionMatrix, 0, 0f, width.toFloat(), height.toFloat(), 0f, -1f, 1f)
        }

        fun onSurfaceCreated() {
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

            programHandle = GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER))
                GLES20.glAttachShader(it, loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER))
                GLES20.glLinkProgram(it)
            }

            positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
            texCoordHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate")
            mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
            textureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")

            updateViewport(width, height)
        }

        fun onDrawFrame() {
            synchronized(this) {
                if (mapObjects.isEmpty()) return

                GLES20.glUseProgram(programHandle)

                GLES20.glEnableVertexAttribArray(positionHandle)
                GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, quadVertices)

                GLES20.glEnableVertexAttribArray(texCoordHandle)
                GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, quadTexCoords)

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glUniform1i(textureHandle, 0)

                mapObjects.forEach { obj ->
                    textureMap[obj.bitmap]?.let { textureId ->
                        val modelMatrix = FloatArray(16)
                        Matrix.setIdentityM(modelMatrix, 0)
                        Matrix.translateM(modelMatrix, 0, obj.screenX + obj.offsetX, obj.screenY + obj.offsetY, 0f)
                        Matrix.scaleM(modelMatrix, 0, (obj.width ?: obj.bitmap.width).toFloat(), (obj.height ?: obj.bitmap.height).toFloat(), 1f)

                        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)

                        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
                        
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    }
                }

                GLES20.glDisableVertexAttribArray(positionHandle)
                GLES20.glDisableVertexAttribArray(texCoordHandle)
            }
        }
        
        fun release() {
            val textureIds = textureMap.values.toIntArray()
            if (textureIds.isNotEmpty()) {
                GLES20.glDeleteTextures(textureIds.size, textureIds, 0)
            }
            textureMap.clear()
        }

        private fun loadTexture(bitmap: Bitmap) {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            val textureId = textures[0]

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            
            textureMap[bitmap] = textureId
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }

        companion object {
            private const val VERTEX_SHADER = """
                uniform mat4 u_MVPMatrix;
                attribute vec2 a_Position;
                attribute vec2 a_TexCoordinate;
                varying vec2 v_TexCoordinate;
                void main() {
                    v_TexCoordinate = a_TexCoordinate;
                    gl_Position = u_MVPMatrix * vec4(a_Position, 0.0, 1.0);
                }
            """

            private const val FRAGMENT_SHADER = """
                precision mediump float;
                uniform sampler2D u_Texture;
                varying vec2 v_TexCoordinate;
                void main() {
                    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
                }
            """
        }
    }
}