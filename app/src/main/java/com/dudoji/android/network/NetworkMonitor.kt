package com.dudoji.android.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dudoji.android.util.NoNetWorkUtil

class NetworkMonitor(private val context: Context) : LifecycleObserver{//안드로이드 생명 주기 감지 옵저버 상속

    private var networkChangeReciver: BroadcastReceiver?= null //네트워크 상태 변경 감지 변수

    var onNetworkAvailable: (() -> Unit)? = null //네트워크 연결되었을 때 실행되는 콜백 함수
    var onNetworkLost: (() -> Unit)? = null //네트워크 끊겼을 때 실행되는 콜백 함수

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) //엑티비티가 resume 상태일 때 호출
    fun startMonitoring(){
        //networkChangeReciver가 null이면 새 BroadcastReceiver 생성시킴
        if(networkChangeReciver == null) {
            networkChangeReciver = object : BroadcastReceiver(){
                //네트워크가 변경 될 때마다 호출 됨
                override fun onReceive(context: Context?, intent: Intent?){
                    checkNetworkState()
                }
            }
        }

        context.registerReceiver(
            networkChangeReciver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)//네트워크 연결 상태가 변경될 때 발생
        )

        checkNetworkState()
    }

    //엑티비티가 pause 상태일 때 호출, 불필요한 리소스 사용 방지
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)

    //리시버 등록을 해제
    fun stopMonitoring(){
        networkChangeReciver?.let{
            context.unregisterReceiver(it)
            networkChangeReciver = null
        }
    }

    //현재 네트워크 상태 확인 함수
    private fun checkNetworkState(){
        if(NoNetWorkUtil(context).isNetworkAvailable()){
            onNetworkAvailable?.invoke()
        }else{
            onNetworkLost?.invoke()
        }
    }
}