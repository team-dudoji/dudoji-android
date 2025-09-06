package com.dudoji.android.presentation.login.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dudoji.android.presentation.login.activity.AutoLoginActivity
import com.dudoji.android.presentation.login.activity.LoginActivity
import com.dudoji.android.presentation.util.getEncryptedPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.GeneralSecurityException

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    val navigationTarget = MutableLiveData<Class<out Activity>>()
    private val PREFS_FILENAME = "secret_prefs"

    fun decideNextActivity() {
        viewModelScope.launch {
            delay(2000)

            var jwt: String? = null
            try {
                val context = getApplication<Application>().applicationContext
                val prefs = getEncryptedPrefs(context)
                jwt = prefs.getString("jwt", null)

            } catch (e: GeneralSecurityException) {
                getApplication<Application>().deleteSharedPreferences(PREFS_FILENAME)
                jwt = null
            } catch (e: Exception) {
                jwt = null
            }

            if (jwt.isNullOrEmpty()) {
                navigationTarget.value = LoginActivity::class.java
            } else {
                navigationTarget.value = AutoLoginActivity::class.java
            }
        }
    }
}