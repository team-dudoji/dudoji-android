package com.dudoji.android.login.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.login.util.getEncryptedPrefs
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.network.NetworkInitializer
import com.dudoji.android.network.RetrofitClient
import com.dudoji.android.network.utils.NoNetWorkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoLoginActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_login)

        NoNetWorkUtil(this).checkNetworkAndNavigate()

        NetworkInitializer.initNonAuthed(this)

        CoroutineScope(Dispatchers.Main).launch {
            tryAutoLogin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun tryAutoLogin() {
        val prefs = getEncryptedPrefs(this)
        val jwt = prefs.getString("jwt", null)
        if (jwt.isNullOrEmpty()) {
            navigateToLogin()
            return
        }

        try {
            val response = RetrofitClient.loginApiService.validateJwt("Bearer $jwt")
            if (response.isSuccessful) {
                NetworkInitializer.initAuthed(this)
                startActivity(Intent(this, MapActivity::class.java))
                finish()
            } else {
                Log.e("JWT", "Invalid JWT: ${response.code()}")
                navigateToLogin()
            }
        } catch (e: Exception) {
            Log.e("JWT", "Network error: ${e.message}")
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

