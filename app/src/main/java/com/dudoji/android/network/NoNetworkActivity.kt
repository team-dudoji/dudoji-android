package com.dudoji.android.network

import com.dudoji.android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.NavigatableActivity

class NoNetworkActivity :  NavigatableActivity(){

    // 네비게이션이 필요 없는 액티비티이므로 빈  mapOf 화면 반환
    override val navigationItems = mapOf<Int, Class<out AppCompatActivity>?>()
    override val defaultSelectedItemId = R.id.mapFragment // 기본 값

    override fun onCreate(saveInstanceState: Bundle?){
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_network)
    }

    override fun setupNetworkMonitoring() {
        networkMonitor.onNetworkAvailable = {
            finish()
        }
    }
}