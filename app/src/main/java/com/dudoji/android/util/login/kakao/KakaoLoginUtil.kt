package com.dudoji.android.util.login.kakao

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dudoji.android.BuildConfig
import com.dudoji.android.map.MapActivity
import com.dudoji.android.util.NetWorkUtil
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


object KakaoLoginUtil {
    val TAG = "KakaoLoginUtil"

    val callback: (OAuthToken?, Throwable?, Context) -> Unit = { token, error, context ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            onLoginSuccess(token, context)
        }
    }

    fun onLoginSuccess(token: OAuthToken, context: Context) {
        Log.i(TAG, "로그인 성공 ${token.accessToken}")
        // /auth/login/kakao/get_token

        val url = HttpUrl.Builder()
            .scheme("http")
            .host(BuildConfig.HOST_IP_ADDRESS)
            .port(8000)
            .addPathSegment("auth/login/kakao/get_token") // URL 경로 추가 (예: /search)
            .addQueryParameter("token", token.accessToken) // ?query=kakao login
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        NetWorkUtil.client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("KakaoLoginUtil", "request fail: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    Log.d("KakaoLoginUtil", "call success: ${response.body?.string()}")
                } else{
                    Log.e("KakaoLoginUtil", "call fail: ${response.code}")
                }
            }

        })

        val intent = Intent(context, MapActivity::class.java)
        context.startActivity(intent)
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
                    onLoginSuccess(token, context)
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