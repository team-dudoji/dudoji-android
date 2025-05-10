package com.dudoji.android.network.activity

import android.os.Bundle
import com.dudoji.android.R
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.network.utils.NetworkMonitor

class NoNetworkActivity : AppCompatActivity() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)

        setupNetworkMonitoring()
    }

    private fun setupNetworkMonitoring() {
        networkMonitor = NetworkMonitor(this)
        lifecycle.addObserver(networkMonitor)

        networkMonitor.onNetworkAvailable = {
            // 네트워크 복구 시 현재 NoNetworkActivity 종료
            finish()
        }
    }
}
