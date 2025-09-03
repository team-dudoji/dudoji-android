package com.dudoji.android.data.network

import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val USER_AGENT = "User-Agent"

class UserAgentInterceptor @Inject constructor(
    @ApplicationContext val context: Context
): okhttp3.Interceptor {

    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val osVersion = Build.VERSION.RELEASE

        Log.d(
            "NetworkInitializer",
            "User-Agent: Dudoji/$appVersion (Android $osVersion; ${Build.MODEL})"
        )

        val request = chain.request().newBuilder()
            .header(USER_AGENT, "Dudoji/${appVersion} (Android ${osVersion}; ${Build.MODEL})")
            .build()
        Log.d("NetworkInitializer", "Request URL: ${request.url}")

        return chain.proceed(request)
    }
}