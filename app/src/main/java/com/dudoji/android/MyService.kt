package com.dudoji.android

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {

    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        job = CoroutineScope(Dispatchers.Default).launch {
            var count = 0
            while(true) {
                Log.d(TAG, "count: $count")
                delay(1000)
                ++count
            }
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null 
    }
}