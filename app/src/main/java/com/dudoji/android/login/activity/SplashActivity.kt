package com.dudoji.android.login.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.dudoji.android.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImage = findViewById<ImageView>(R.id.splash_logo_image)
        val dudojiImage = findViewById<ImageView>(R.id.dudoji_image)

        logoImage.load("file:///android_asset/splash/splash_logo.png")
        dudojiImage.load("file:///android_asset/splash/dudoji.png")

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AutoLoginActivity::class.java))
            finish()
        }, 2000)
    }
}