package com.dudoji.android

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dudoji.android.databinding.ActivityMainBinding
import com.dudoji.android.map.MapActivity
import com.dudoji.android.util.NoNetWorkUtil
import com.dudoji.android.util.RequestPermissionsUtil
import com.dudoji.android.util.login.kakao.KakaoLoginUtil
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var kakaoLoginButton: Button

    override fun onStart() {
        super.onStart()
        RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        NoNetWorkUtil(this).checkNetworkAndNavigate()
    }


        setKakaoLoginButton()
    }

    fun setKakaoLoginButton(){
        kakaoLoginButton = findViewById<Button>(R.id.kakao_login_button)
        kakaoLoginButton.setOnClickListener(){
            KakaoLoginUtil.loginWithKakao(this)
        }
    }
}
