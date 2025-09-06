package com.dudoji.android.presentation.login.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dudoji.android.data.oauth.kakao.KakaoLoginUtil
import com.dudoji.android.network.NetworkInitializer
import com.dudoji.android.presentation.map.MapActivity
import com.dudoji.android.presentation.util.getEncryptedPrefs
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userApiService = RetrofitClient.loginApiService
    val navigationEvent = MutableLiveData<Intent>()

    fun onKakaoLoginClicked() {
        val context = getApplication<Application>().applicationContext

        KakaoLoginUtil.tryLoginWithKakao(context) { token, error ->
            if (error != null) {
                Log.e("LoginViewModel", "카카오 로그인 실패", error)
                return@tryLoginWithKakao
            }
            if (token != null) {
                loginToDudojiServer(token.accessToken)
            }
        }
    }

    private fun loginToDudojiServer(kakaoAccessToken: String) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            try {
                val response = userApiService.kakaoLogin(kakaoAccessToken)
                if (response.isSuccessful) {
                    val jwt = response.body()?.token?.accessToken

                    val prefs = getEncryptedPrefs(context)
                    prefs.edit().putString("jwt", jwt).apply()

                    NetworkInitializer.initAuthed(context)

                    val intent = Intent(context, MapActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    navigationEvent.postValue(intent)

                } else {
                    Log.e("LoginViewModel", "두도지 서버 로그인 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "두도지 서버 로그인 중 예외 발생", e)
            }
        }
    }

    fun onHelpClicked() {
        val context = getApplication<Application>().applicationContext
        Toast.makeText(context, "도움말 기능 준비 중", Toast.LENGTH_SHORT).show()
    }
}