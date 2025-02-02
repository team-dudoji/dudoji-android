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
            repeat(100) { i ->
                Log.d(TAG, "count: $i")
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}