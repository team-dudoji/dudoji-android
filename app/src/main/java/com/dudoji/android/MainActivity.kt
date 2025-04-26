package com.dudoji.android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dudoji.android.databinding.ActivityMainBinding
import com.dudoji.android.network.NetworkChecker
import com.dudoji.android.network.NoNetworkActivity
import com.dudoji.android.oauth.kakao.KakaoLoginUtil
import com.dudoji.android.util.permission.RequestPermissionsUtil

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
        checkNetworkAndNavigate()

        setKakaoLoginButton()
    }

    fun setKakaoLoginButton() {
        kakaoLoginButton = findViewById<Button>(R.id.kakao_login_button)
        kakaoLoginButton.setOnClickListener(){
            KakaoLoginUtil.loginWithKakao(this)
        }
    }

    fun checkNetworkAndNavigate() {
        if (!NetworkChecker.isNetworkAvailable(this)) {
            // 네트워크가 없는 경우 NoNetworkActivity로 이동
            val intent = Intent(this, NoNetworkActivity::class.java)
            this.startActivity(intent)
            this.finish() // 액티비티 종료
        }
    }
}
