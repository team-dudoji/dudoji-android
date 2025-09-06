package com.dudoji.android.presentation.login.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.R
import com.dudoji.android.data.oauth.kakao.KakaoLoginUtil
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.network.NetworkInitializer
import com.dudoji.android.network.utils.NoNetWorkUtil
import com.dudoji.android.presentation.util.MandatoryPermissionHandler
import com.dudoji.android.presentation.util.RequestPermissionsUtil
import com.dudoji.android.presentation.util.getEncryptedPrefs
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), MandatoryPermissionHandler.PermissionResultListener {

    private lateinit var kakaoLoginButton: ImageButton
    private lateinit var helpTextView: TextView
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
        setContentView(R.layout.activity_login)

        requestPermissionsUtil = RequestPermissionsUtil(this)
        permissionHandler = MandatoryPermissionHandler(this, this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NoNetWorkUtil(this).checkNetworkAndNavigate()

        NetworkInitializer.initNonAuthed(this@LoginActivity)

        kakaoLoginButton = findViewById(R.id.btn_kakao_login)
        helpTextView = findViewById(R.id.tv_help)

        setClickListeners()

        lifecycleScope.launch {
            tryAutoLogin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        kakaoLoginButton.setOnClickListener {
            KakaoLoginUtil.tryLoginWithKakao(this)
        }

        helpTextView.setOnClickListener {
            Toast.makeText(this, "도움말 기능 준비 중", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun tryAutoLogin() {
        val prefs = getEncryptedPrefs(this)
        val jwt = prefs.getString("jwt", null)
        if (!jwt.isNullOrEmpty()) {
            try {
                val response = RetrofitClient.loginApiService.validateJwt("Bearer $jwt")
                if (response.isSuccessful) {
                    NetworkInitializer.initAuthed(this)
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("JWT_LOGIN", "자동 로그인 실패: 유효하지 않은 토큰 (코드: ${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("JWT_LOGIN", "자동 로그인 중 오류 발생: ${e.message}")
            }
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
}