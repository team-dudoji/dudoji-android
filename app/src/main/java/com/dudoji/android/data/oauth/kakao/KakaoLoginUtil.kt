package com.dudoji.android.data.oauth.kakao

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

object KakaoLoginUtil {
    val TAG = "KakaoLoginUtilDEBUG"

    fun tryLoginWithKakao(
        context: Context,
        onResult: (token: OAuthToken?, error: Throwable?) -> Unit
    ) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            onResult(token, error)
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(context, callback)
        } else {
            loginWithKakaoAccount(context, callback)
        }
    }

    private fun loginWithKakaoAccount(
        context: Context,
        callback: (OAuthToken?, Throwable?) -> Unit
    ) {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }

    private fun loginWithKakaoTalk(
        context: Context,
        callback: (OAuthToken?, Throwable?) -> Unit
    ) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    callback(null, error)
                    return@loginWithKakaoTalk
                }
                loginWithKakaoAccount(context, callback)
            } else if (token != null) {
                callback(token, null)
            }
        }
    }
}