package com.dudoji.android.login.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityLoginBinding
import com.dudoji.android.login.oauth.kakao.KakaoLoginUtil
import com.dudoji.android.login.permission.MandatoryPermissionHandler
import com.dudoji.android.login.permission.RequestPermissionsUtil
import com.dudoji.android.network.utils.NoNetWorkUtil

class LoginActivity : AppCompatActivity(), MandatoryPermissionHandler.PermissionResultListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var kakaoLoginButton: Button
    private lateinit var requestPermissionsUtil: RequestPermissionsUtil
    private lateinit var permissionHandler: MandatoryPermissionHandler

    override fun onStart() {
        super.onStart()
        requestPermissionsUtil.requestInitialPermissions() //초기 권한 값 한꺼번에(위치, 카메라, 사진)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionsUtil = RequestPermissionsUtil(this)
        permissionHandler = MandatoryPermissionHandler(this, this)

        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        NoNetWorkUtil(this).checkNetworkAndNavigate()
        setKakaoLoginButton()
    }

    fun setKakaoLoginButton() {
        kakaoLoginButton = findViewById<Button>(R.id.kakao_login_button)
        kakaoLoginButton.setOnClickListener(){
            KakaoLoginUtil.loginWithKakao(this)
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
