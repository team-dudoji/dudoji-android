package com.dudoji.android.network.activity

import com.dudoji.android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.network.utils.NetworkMonitor

class NoNetworkActivity : AppCompatActivity() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)

        // 네트워크 모니터 초기화 및 감시 시작
        networkMonitor = NetworkMonitor(this)
        lifecycle.addObserver(networkMonitor)

        networkMonitor.onNetworkAvailable = {
            finish()
        }
    }
}
