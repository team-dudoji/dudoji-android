package com.dudoji.android.oauth.kakao

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dudoji.android.map.MapActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object KakaoLoginUtil {
    val TAG = "KakaoLoginUtilDEBUG"
    private val userApiService = RetrofitClient.userApiService

    val callback: (OAuthToken?, Throwable?, Context) -> Unit = { token, error, context ->
        if (error != null) {
            Log.e(TAG, "Unsuccess to kakao login", error)
        } else if (token != null) {
            CoroutineScope(Dispatchers.Main).launch {
                onLoginSuccess(token, context)
            }
        }
    }

    suspend fun onLoginSuccess(token: OAuthToken, context: Context) {
        Log.d(TAG, "Success to Kakao login")
        val response = userApiService.kakaoLogin(token.accessToken)

        if (response.isSuccessful) {
            Log.d(TAG, "Success to dudoji login ${response.body()}")
            val accessToken = response.body()?.token?.accessToken

            val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            prefs.edit().putString("jwt", accessToken).apply()
            RetrofitClient.init(context)
            val intent = Intent(context, MapActivity::class.java)
            context.startActivity(intent)
        } else {
            Log.e(TAG, "Unsuccess to dudoji login ${response.code()}")
        }
    }

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
    }
}
