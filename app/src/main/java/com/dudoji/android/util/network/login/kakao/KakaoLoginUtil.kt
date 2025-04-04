package com.dudoji.android.util.network.login.kakao

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
    val TAG = "KakaoLoginUtil"
    private val userApiService = RetrofitClient.userApiService

    val callback: (OAuthToken?, Throwable?, Context) -> Unit = { token, error, context ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            CoroutineScope(Dispatchers.Main).launch {
                onLoginSuccess(token, context)
            }
        }
    }

    suspend fun onLoginSuccess(token: OAuthToken, context: Context) {
        Log.i(TAG, "로그인 성공 ${token.accessToken}")

        val response = userApiService.kakaoLogin(token.accessToken)

        if (response.isSuccessful) {
            Log.i(TAG, "카카오 로그인 성공")
            val intent = Intent(context, MapActivity::class.java)
            context.startActivity(intent)
        } else {
            Log.e(TAG, "카카오 로그인 실패 ${response.code()}")
        }
    }

    fun loginWithKakao(context: Context) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "kakao login is unsuccessful", error)

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