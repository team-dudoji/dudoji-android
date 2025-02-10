package com.dudoji.android.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.dudoji.android.DBHelper
import com.dudoji.android.R
import com.google.android.ads.mediationtestsuite.activities.HomeActivity

//view 데이터를 묶어줌
class LoginViewBinder(private val activity: AppCompatActivity) {
    val btnLogin: Button by lazy { activity.findViewById(R.id.btnLogin) }
    val editTextId: EditText by lazy { activity.findViewById(R.id.editTextId) }
    val editTextPassword: EditText by lazy { activity.findViewById(R.id.editTextPassword) }
    val btnRegister: Button by lazy { activity.findViewById(R.id.btnRegister) }
}

// 로그인시 데베와 비교하는 기능
class LoginHandler(
    private val context: Context,
    private val dbHelper: DBHelper,
    private val navigator: LoginNavigator
) {
    fun handleLogin(user: String, pass: String) {
        when {
            user.isBlank() || pass.isBlank() -> showToast("아이디와 비밀번호를 모두 입력해주세요.")
            dbHelper.checkUserpass(user, pass) -> loginSuccess()
            else -> showToast("아이디와 비밀번호를 확인해 주세요.")
        }
    }

    private fun loginSuccess() {
        showToast("로그인 되었습니다.")
        navigator.navigateToHome()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

//화면 전환
interface LoginNavigator {
    fun navigateToHome()
    fun navigateToRegister()
}

//로그인 기능
class LoginActivity : AppCompatActivity(), LoginNavigator {
    private lateinit var viewBinder: LoginViewBinder
    private lateinit var loginHandler: LoginHandler
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)
        viewBinder = LoginViewBinder(this)
        loginHandler = LoginHandler(this, dbHelper, this)

        setupLoginButton()
        setupRegisterButton()
    }

    private fun setupLoginButton() {
        viewBinder.btnLogin.setOnClickListener {
            val user = viewBinder.editTextId.text.toString()
            val pass = viewBinder.editTextPassword.text.toString()
            loginHandler.handleLogin(user, pass)
        }
    }

    private fun setupRegisterButton() {
        viewBinder.btnRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}