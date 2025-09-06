package com.dudoji.android.presentation.login.viewmodel


import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dudoji.android.network.NetworkInitializer
import com.dudoji.android.presentation.login.activity.LoginActivity
import com.dudoji.android.presentation.map.MapActivity
import com.dudoji.android.presentation.util.getEncryptedPrefs
import kotlinx.coroutines.launch

class AutoLoginViewModel(application: Application) : AndroidViewModel(application) {

    val navigationTarget = MutableLiveData<Intent>()

    fun validateToken() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val prefs = getEncryptedPrefs(context)
            val jwt = prefs.getString("jwt", null)

            if (jwt.isNullOrEmpty()) {
                // 토큰이 없으면 바로 로그인 화면으로
                val intent = Intent(context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                navigationTarget.value = intent
                return@launch
            }

            try {
                val response = RetrofitClient.loginApiService.validateJwt("Bearer $jwt")
                if (response.isSuccessful) {
                    NetworkInitializer.initAuthed(context)
                    val intent = Intent(context, MapActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    navigationTarget.value = intent
                } else {
                    Log.e("JWT", "Invalid JWT: ${response.code()}")
                    navigateToLogin(context)
                }
            } catch (e: Exception) {
                Log.e("JWT", "Network error: ${e.message}")
                navigateToLogin(context)
            }
        }
    }

    private fun navigateToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        navigationTarget.value = intent
    }
}