package com.dudoji.android.login.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityLoginBinding
import com.dudoji.android.login.oauth.kakao.KakaoLoginUtil
import com.dudoji.android.login.util.MandatoryPermissionHandler
import com.dudoji.android.login.util.RequestPermissionsUtil
import com.dudoji.android.login.util.getEncryptedPrefs
import com.dudoji.android.presentation.map.MapActivity
import com.dudoji.android.network.NetworkInitializer
import com.dudoji.android.network.utils.NoNetWorkUtil
import kotlinx.coroutines.launch
import java.io.IOException // import 추가

class LoginActivity : AppCompatActivity(), MandatoryPermissionHandler.PermissionResultListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var kakaoLoginButton: Button
    private lateinit var requestPermissionsUtil: RequestPermissionsUtil
    private lateinit var permissionHandler: MandatoryPermissionHandler

    override fun onStart() {
        super.onStart()
        requestPermissionsUtil.requestInitialPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionsUtil = RequestPermissionsUtil(this)
        permissionHandler = MandatoryPermissionHandler(this, this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        NoNetWorkUtil(this).checkNetworkAndNavigate()

        NetworkInitializer.initNonAuthed(this@LoginActivity)

        findViewById<ImageView>(R.id.imageView).load("file:///android_asset/login/login_logo.png")
        kakaoLoginButton = findViewById(R.id.kakao_login_button)
        try {
            val kakaoBg = assets.open("login/kakao_login_medium_wide.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            kakaoLoginButton.background = kakaoBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

        setKakaoLoginButton()

        lifecycleScope.launch{
            tryLogin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setKakaoLoginButton() {
        kakaoLoginButton.setOnClickListener(){
            KakaoLoginUtil.tryLoginWithKakao(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.handlePermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAppShouldBeTerminated() {
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun tryLogin() {
        val prefs = getEncryptedPrefs(this)
        val jwt = prefs.getString("jwt", null)
        if (jwt != null) {
            try {
                val response = RetrofitClient.loginApiService.validateJwt("Bearer $jwt")
                if (response.isSuccessful) {
                    NetworkInitializer.initAuthed(this)
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("JWT", "Invalid JWT: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JWT", "Network error: ${e.message}")
            }
        }
    }
}