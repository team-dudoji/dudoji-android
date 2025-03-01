package com.dudoji.android.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.network.NoNetworkActivity

class NoNetWorkUtil(private val context: Context) {

    //네트워크 확인 함수데스요
    fun isNetworkAvailable(): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager //네트워크 관리 서비스를 가져워 ConnectivityManager 타입으로 캐스팅 해줌

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //빌드 버전 확인
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) //와이파이 연결됨?
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) //셀룰러 데이터 연결됨?
        } else{ //안드로이드 6이하
            val activieNetworkInfo = connectivityManager.activeNetworkInfo
            activieNetworkInfo != null && activieNetworkInfo.isConnected
        }

    }

    fun checkNetworkAndNavigate() {
        if (!isNetworkAvailable()) {
            // 네트워크가 없는 경우 NoNetworkActivity로 이동
            val intent = Intent(context, NoNetworkActivity::class.java)
            context.startActivity(intent)
            if (context is AppCompatActivity) {
                (context as AppCompatActivity).finish() // 액티비티 종료
            }
        }
    }
}