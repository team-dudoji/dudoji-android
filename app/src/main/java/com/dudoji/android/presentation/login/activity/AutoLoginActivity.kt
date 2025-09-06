package com.dudoji.android.presentation.login.activity

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.dudoji.android.R
import com.dudoji.android.presentation.login.viewmodel.AutoLoginViewModel

@RequiresApi(Build.VERSION_CODES.O)
class AutoLoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AutoLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_login)
        viewModel = ViewModelProvider(this).get(AutoLoginViewModel::class.java)

        findViewById<ImageView>(R.id.imageView).load("file:///android_asset/login/login_logo.png")

        viewModel.navigationTarget.observe(this) { intent ->
            startActivity(intent)
            finish()
        }

        viewModel.validateToken()
    }
}