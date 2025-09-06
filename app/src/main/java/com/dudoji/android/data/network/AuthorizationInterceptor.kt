package com.dudoji.android.data.network

import android.content.Context
import android.util.Log
import com.dudoji.android.presentation.util.getEncryptedPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Response
import javax.inject.Inject

const val AUTHORIZATION = "Authorization"

class AuthorizationInterceptor @Inject constructor(
    @ApplicationContext val context: Context
) : okhttp3.Interceptor {

    override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
        val prefs = getEncryptedPrefs(context)
        val token = prefs.getString("jwt", null)

        if (token.isNullOrEmpty()) {
            Log.w("NetworkInitializer", "No JWT token found in preferences")
            return chain.proceed(chain.request())
        }

        val request = chain.request().newBuilder()
            .addHeader(AUTHORIZATION, "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}