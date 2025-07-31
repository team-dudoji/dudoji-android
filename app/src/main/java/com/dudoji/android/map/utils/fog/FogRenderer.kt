package com.dudoji.android.map.utils.fog

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import com.dudoji.android.R
import com.dudoji.android.config.FOG_PARTICLE_SIZE
import com.dudoji.android.map.domain.FogParticle
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Deprecated(message = "use Fog Particle Overlay View")
class FogRenderer(private val context: Context) {

    // 사각형(quad) 정점 좌표 (x,y,z)
    private val vertices = floatArrayOf(
        -FOG_PARTICLE_SIZE/2f, FOG_PARTICLE_SIZE/2f, 0f,
        -FOG_PARTICLE_SIZE/2f, -FOG_PARTICLE_SIZE/2f, 0f,
        FOG_PARTICLE_SIZE/2f, FOG_PARTICLE_SIZE/2f, 0f,
        FOG_PARTICLE_SIZE/2f, -FOG_PARTICLE_SIZE/2f, 0f
    )

    // 텍스처 좌표 (s,t)
    private val texCoords = floatArrayOf(
        0f, 0f,  // 왼쪽 위
        0f, 1f,  // 왼쪽 아래
        1f, 0f,  // 오른쪽 위
        1f, 1f   // 오른쪽 아래
    )


    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer

    private var program = 0
    private var textureId = 0

    private var surfaceWidth = 1
    private var surfaceHeight = 1

    // 매트릭스 관련
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    var particles: List<FogParticle> = emptyList()

    // 셰이더 코드 (모델뷰투영 매트릭스 추가)
    private val vertexShaderCode = """
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        uniform mat4 uMVPMatrix;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vTexCoord = aTexCoord;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform float u_Alpha;
        uniform sampler2D uTexture;
        void main() {
            vec4 color = texture2D(uTexture, vTexCoord);
            color.a *= u_Alpha;
            if(color.a < 0.1) discard;
            gl_FragColor = color;
        }
    """

    fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(vertices)
                position(0)
            }

        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(texCoords)
                position(0)
            }

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        GLES20.glClearColor(0f, 0f, 0f, 0f) // 투명 배경

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST) // 필요 없으면 depth test 비활성화

        textureId = loadTexture(context, R.drawable.fog_particle) // 텍스처 로드
    }

    fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height

        GLES20.glViewport(0, 0, width, height)

        // orthographic projection 0, width / 0, height 를 OpenGL 좌표로 변환
        Matrix.orthoM(projectionMatrix, 0, 0f, width.toFloat(), height.toFloat(), 0f, -1f, 1f)
    }

    fun onDrawFrame(unused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(program)

        val posHandle = GLES20.glGetAttribLocation(program, "aPosition")
        val texHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val samplerHandle = GLES20.glGetUniformLocation(program, "uTexture")
        val alphaHandle = GLES20.glGetUniformLocation(program, "u_Alpha")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(samplerHandle, 0)

        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glEnableVertexAttribArray(texHandle)

        particles.forEach { particle ->
//            particle.updateAlpha()
//            if (particle.alpha == 0f) return@forEach
            if (!particle.visible) return@forEach
            // 모델 행렬 초기화
            Matrix.setIdentityM(modelMatrix, 0)

            // 화면 좌표 그대로 쓴다 (픽셀 단위)
            // GLSurfaceView 좌표계는 왼쪽 위 (0,0), y가 아래 방향
            Matrix.translateM(modelMatrix, 0, particle.screenPoint.x.toFloat(), particle.screenPoint.y.toFloat(), 0f)

            // 원하는 크기 조절 (필요하면 변경)
            val scale = 20f  // 20픽셀 크기
            Matrix.scaleM(modelMatrix, 0, scale, scale, 1f)

            // projection * model = mvp
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)

            GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }
        Log.d("FogTextureView", "Drawn visible particles: $particles")

        GLES20.glDisableVertexAttribArray(posHandle)
        GLES20.glDisableVertexAttribArray(texHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            // 컴파일 체크 (생략 가능)
        }
    }

    private fun loadTexture(context: Context, resId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) throw RuntimeException("Failed to generate texture")

        val options = BitmapFactory.Options().apply { inScaled = false }
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureIds[0]
    }
}
