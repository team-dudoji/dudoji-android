package com.dudoji.android.presentation.login.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.dudoji.android.R
import com.dudoji.android.presentation.util.getEncryptedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.GeneralSecurityException

@RequiresApi(Build.VERSION_CODES.O)
class SplashActivity : AppCompatActivity() {

    private val PREFS_FILENAME = "secret_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImage = findViewById<ImageView>(R.id.splash_logo_image)
        val dudojiImage = findViewById<ImageView>(R.id.dudoji_image)

        logoImage.load("file:///android_asset/splash/splash_logo.png")
        dudojiImage.load("file:///android_asset/splash/dudoji.png")

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)

            var jwt: String? = null
            try {
                val prefs = getEncryptedPrefs(this@SplashActivity)
                jwt = prefs.getString("jwt", null)

            } catch (e: GeneralSecurityException) {
                Log.e("ENCRYPTION_ERROR", "암호화된 SharedPreferences 로딩 실패. 파일을 삭제하고 다시 시도합니다.", e)

                deleteSharedPreferences(PREFS_FILENAME)

                jwt = null

            } catch (e: Exception) {
                Log.e("SPLASH_ERROR", "알 수 없는 오류 발생", e)
                jwt = null
            }

            if (jwt.isNullOrEmpty()) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, AutoLoginActivity::class.java))
            }

            finish()
        }
    }
}