package com.dudoji.android.login.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.dudoji.android.R
import com.dudoji.android.login.util.getEncryptedPrefs
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.network.NetworkInitializer
import com.dudoji.android.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.annotation.RequiresApi

class SplashActivity : AppCompatActivity() {
    private val splashScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImage = findViewById<ImageView>(R.id.splash_logo_image)
        val dudojiImage = findViewById<ImageView>(R.id.dudoji_image)

        // 로그인 화면과 동일한 로고 사용
        logoImage.load("file:///android_asset/login/login_logo.png")
        dudojiImage.load("file:///android_asset/splash/dudoji.png")

        // 네트워크 비인증 초기화 (JWT 검증 이전 단계)
        NetworkInitializer.initNonAuthed(this)

        // 자동 로그인 시도 후 실패 시 로그인 화면으로 이동
        splashScope.launch {
            tryAutoLogin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun tryAutoLogin() {
        val prefs = getEncryptedPrefs(this)
        val jwt = prefs.getString("jwt", null)
        if (jwt.isNullOrEmpty()) {
            // JWT 없으면 로그인 화면으로 이동 (2초 스플래시 유지)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 2000)
            return
        }

        try {
            val response = RetrofitClient.loginApiService.validateJwt("Bearer $jwt")
            if (response.isSuccessful) {
                // 인증 초기화 후 지도 화면으로 이동
                NetworkInitializer.initAuthed(this)
                startActivity(Intent(this, MapActivity::class.java))
                finish()
            } else {
                // 토큰 무효: 로그인 화면으로 이동
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            // 네트워크 오류 시 로그인 화면으로
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}