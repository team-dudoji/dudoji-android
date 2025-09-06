package com.dudoji.android

import android.app.Application
import com.dudoji.android.network.NetworkInitializer
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        NetworkInitializer.initNonAuthed(this)
    }
}