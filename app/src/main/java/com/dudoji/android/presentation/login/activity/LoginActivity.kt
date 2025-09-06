package com.dudoji.android.presentation.login.activity

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dudoji.android.R
import com.dudoji.android.presentation.login.viewmodel.LoginViewModel
import com.dudoji.android.presentation.util.MandatoryPermissionHandler
import com.dudoji.android.presentation.util.RequestPermissionsUtil

@RequiresApi(Build.VERSION_CODES.O)
class LoginActivity : AppCompatActivity(), MandatoryPermissionHandler.PermissionResultListener {

    private lateinit var kakaoLoginButton: ImageButton
    private lateinit var helpTextView: TextView
    private lateinit var requestPermissionsUtil: RequestPermissionsUtil
    private lateinit var permissionHandler: MandatoryPermissionHandler
    private lateinit var viewModel: LoginViewModel

    override fun onStart() {
        super.onStart()
        requestPermissionsUtil.requestInitialPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        requestPermissionsUtil = RequestPermissionsUtil(this)
        permissionHandler = MandatoryPermissionHandler(this, this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        kakaoLoginButton = findViewById(R.id.btn_kakao_login)
        helpTextView = findViewById(R.id.tv_help)

        viewModel.navigationEvent.observe(this) { intent ->
            startActivity(intent)
            finish()
        }

        setClickListeners()
    }

    private fun setClickListeners() {
        kakaoLoginButton.setOnClickListener {
            viewModel.onKakaoLoginClicked()
        }

        helpTextView.setOnClickListener {
            viewModel.onHelpClicked()
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