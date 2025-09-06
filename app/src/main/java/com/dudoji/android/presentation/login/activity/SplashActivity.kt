package com.dudoji.android.presentation.login.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.dudoji.android.R
import com.dudoji.android.presentation.login.viewmodel.SplashViewModel

@RequiresApi(Build.VERSION_CODES.O)
class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        val logoImage = findViewById<ImageView>(R.id.splash_logo_image)
        val dudojiImage = findViewById<ImageView>(R.id.dudoji_image)
        logoImage.load("file:///android_asset/splash/splash_logo.png")
        dudojiImage.load("file:///android_asset/splash/dudoji.png")

        viewModel.navigationTarget.observe(this) { destinationActivity ->
            startActivity(Intent(this, destinationActivity))
            finish()
        }

        viewModel.decideNextActivity()
    }
}