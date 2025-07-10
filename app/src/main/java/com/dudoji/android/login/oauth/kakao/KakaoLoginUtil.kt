package com.dudoji.android.login.oauth.kakao

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.login.util.getEncryptedPrefs
import com.dudoji.android.map.activity.MapActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object KakaoLoginUtil {
    val TAG = "KakaoLoginUtilDEBUG"
    @RequiresApi(Build.VERSION_CODES.O)
    private val userApiService = RetrofitClient.loginApiService

    @RequiresApi(Build.VERSION_CODES.O)
    val callback: (OAuthToken?, Throwable?, Context) -> Unit = { token, error, context ->
        if (error != null) {
            Log.e(TAG, "Unsuccess to kakao login", error)
        } else if (token != null) {
            CoroutineScope(Dispatchers.Main).launch {
                onLoginSuccess(token, context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun onLoginSuccess(token: OAuthToken, context: Context) {
        Log.d(TAG, "Success to Kakao login")
        val response = userApiService.kakaoLogin(token.accessToken)

        if (response.isSuccessful) {
            Log.d(TAG, "Success to dudoji login ${response.body()}")
            val accessToken = response.body()?.token?.accessToken

            val prefs = getEncryptedPrefs(context)
            prefs.edit().putString("jwt", accessToken).apply()
            RetrofitClient.init(context)
            val intent = Intent(context, MapActivity::class.java)
            context.startActivity(intent)
        } else {
            Log.e(TAG, "Unsuccess to dudoji login ${response.code()}")

            // for debug =====================================
            RetrofitClient.init(context)
            val intent = Intent(context, MapActivity::class.java)
            context.startActivity(intent)
            // ============================================
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loginWithKakao(context: Context) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "Unsuccess to kakao login ", error)

                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    UserApiClient.instance.loginWithKakaoAccount(context) {
                        token, error ->
                        callback(token, error, context)
                    }
                } else if (token != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onLoginSuccess(token, context)
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) {
                    token, error ->
                callback(token, error, context)
            }
        }

//        // TODO: Re-enable the production Kakao login logic before releasing to production.
        //테스트용
//            RetrofitClient.init(context)
//            val intent = Intent(context, MapActivity::class.java)
//            context.startActivity(intent)
    }
}
