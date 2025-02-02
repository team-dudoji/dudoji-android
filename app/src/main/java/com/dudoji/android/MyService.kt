package com.dudoji.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {
    
    //시작 변수 선언
    companion object {
        private const val NOTI_ID = 1
    }

    //로그 찍기
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startCounting() {
        scope.launch {
            repeat(100) { i ->
                Log.d(TAG, "count: $i")
                delay(1000)
            }
        }
    }

    fun stopCounting() {
        scope.cancel()
    }


    private fun createNotification() {
        val builder = NotificationCompat.Builder(this, "default")

        builder.apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Foreground Service")
            setContentText("포그라운드 서비스")
            color = Color.RED
        }

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        builder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }


        val notification = builder.build()
        notificationManager.notify(NOTI_ID, notification)
        startForeground(NOTI_ID, notification)
    }

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