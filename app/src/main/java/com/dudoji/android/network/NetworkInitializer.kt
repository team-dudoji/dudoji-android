package com.dudoji.android.network

import RetrofitClient
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.login.util.getEncryptedPrefs
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Deprecated("Use Hilt for dependency injection instead", ReplaceWith("Hilt"))
@RequiresApi(Build.VERSION_CODES.O)
object NetworkInitializer {
    const val USER_AGENT = "User-Agent"
    const val AUTHORIZATION = "Authorization"

    fun initNonAuthed(context: Context) {
        val client = provideNonAuthedOkHttpClient(context)

        RetrofitClient.initNonAuthed(client)
    }

    fun initAuthed(context: Context) {

        val client = provideAuthedOkHttpClient(context)

        // Initialize Retrofit client
        RetrofitClient.initAuthed(client)

        // Initialize Coil image loader
        Coil.init(context, client)
    }

    fun provideNonAuthedOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(false)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor(context))
            .build()
    }

    fun provideAuthedOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(false)
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

        Log.d(
            "NetworkInitializer",
            "User-Agent: Dudoji/$appVersion (Android $osVersion; ${Build.MODEL})"
        )

        return okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header(USER_AGENT, "Dudoji/${appVersion} (Android ${osVersion}; ${Build.MODEL})")
                .build()
            Log.d("NetworkInitializer", "Request URL: ${request.url}")
            chain.proceed(request)
        }
    }

    fun authorizationInterceptor(context: Context): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val prefs = getEncryptedPrefs(context)
            val token = prefs.getString("jwt", null)

            if (token.isNullOrEmpty()) {
                Log.w("NetworkInitializer", "No JWT token found in preferences")
                return@Interceptor chain.proceed(chain.request())
            }

            val request = chain.request().newBuilder()
                .addHeader(AUTHORIZATION, "Bearer $token")
                .build()
            chain.proceed(request)
        }
    }
}