package com.dudoji.android.data.network

import RetrofitClient
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.BuildConfig
import com.dudoji.android.data.remote.FollowApiService
import com.dudoji.android.network.utils.LocalDateTimeAdapter
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@RequiresApi(Build.VERSION_CODES.O)
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val BASE_URL = "https://${BuildConfig.HOST_IP_ADDRESS}:${BuildConfig.HOST_PORT}"

    val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @Provides
    @Singleton
    fun provideNonAuthedClient(
        userAgentInterceptor: UserAgentInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(false)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthedOkHttpClient(
        userAgentInterceptor: UserAgentInterceptor,
        authorizationInterceptor: AuthorizationInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(false)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(authorizationInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideNonAuthedRetrofit(
        userAgentInterceptor: UserAgentInterceptor
    ): Retrofit =
        Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideNonAuthedClient(userAgentInterceptor))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideAuthedRetrofit(
        userAgentInterceptor: UserAgentInterceptor,
        authorizationInterceptor: AuthorizationInterceptor
    ): Retrofit =
        Retrofit.Builder()
        .baseUrl(RetrofitClient.BASE_URL)
        .client(provideAuthedOkHttpClient(userAgentInterceptor, authorizationInterceptor))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideFollowApiService(
        retrofit: Retrofit
    ): FollowApiService {
        return retrofit.create(FollowApiService::class.java)
    }
}