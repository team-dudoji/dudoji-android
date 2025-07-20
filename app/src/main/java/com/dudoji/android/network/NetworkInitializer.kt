package com.dudoji.android.network

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.login.util.getEncryptedPrefs
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkInitializer {
    const val USER_AGENT = "User-Agent"
    const val AUTHORIZATION = "Authorization"

    @RequiresApi(Build.VERSION_CODES.O)
    fun initNonAuthed(context: Context) {
        val client = provideNonAuthedOkHttpClient(context)

        RetrofitClient.initNonAuthed(client)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initAuthed(context: Context) {

        val client = provideAuthedOkHttpClient(context)

        // Initialize Retrofit client
        RetrofitClient.initAuthed(client)

        // Initialize Coil image loader
        CoilConfigurer.init(context, client)
    }

    fun provideNonAuthedOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor(context))
            .build()
    }

    fun provideAuthedOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor(context))
            .addInterceptor(authorizationInterceptor(context))
            .build()
    }

    fun userAgentInterceptor(context: Context): okhttp3.Interceptor {
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val osVersion = Build.VERSION.RELEASE

        Log.d("NetworkInitializer", "User-Agent: Dudoji/$appVersion (Android $osVersion; ${Build.MODEL})")

        return okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header(USER_AGENT, "Dudoji/${appVersion} (Android ${osVersion}; ${Build.MODEL})")
                .build()
            chain.proceed(request)
        }
    }

    fun authorizationInterceptor(context: Context): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val prefs = getEncryptedPrefs(context)
            val token = prefs.getString("jwt", null)
                ?: throw IllegalStateException("JWT token not found in preferences")

            val request = chain.request().newBuilder()
                .addHeader(AUTHORIZATION, "Bearer $token")
                .build()
            chain.proceed(request)
        }
    }
}