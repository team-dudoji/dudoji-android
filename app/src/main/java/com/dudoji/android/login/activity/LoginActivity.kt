package com.dudoji.android.login.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityLoginBinding
import com.dudoji.android.login.oauth.kakao.KakaoLoginUtil
import com.dudoji.android.login.permission.RequestPermissionsUtil
import com.dudoji.android.login.util.getEncryptedPrefs
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.network.utils.NoNetWorkUtil
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var kakaoLoginButton: Button

    override fun onStart() {
        super.onStart()
        RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        NoNetWorkUtil(this).checkNetworkAndNavigate()

        setKakaoLoginButton()

        lifecycleScope.launch{
            tryLogin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setKakaoLoginButton() {
        kakaoLoginButton = findViewById<Button>(R.id.kakao_login_button)
        kakaoLoginButton.setOnClickListener(){
            KakaoLoginUtil.loginWithKakao(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun tryLogin() {
        val prefs = getEncryptedPrefs(this)
        val jwt = prefs.getString("jwt", null)
        if (jwt != null) {

            val response = RetrofitClient.loginApiService.validateJwt("Bearer $jwt")

            if (response.code() == 200) {
                RetrofitClient.init(this)

                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
