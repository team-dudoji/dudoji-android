package com.dudoji.android.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.network.NoNetworkActivity
import com.dudoji.android.repository.RevealCircleRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class NetworkUtil(private val context: Context) { //context 추가 했음
    private val client = OkHttpClient()
    private val SERVER_URL = "http://localhost:8000/"

    fun createRevealCirclesRequestJson(): JsonObject{
        val revealCircles = RevealCircleRepository.getLocations()

        val revealCirclesJsonArray = JsonArray()

        for(circle in revealCircles){
            val revealCircle = JsonObject().apply {
                addProperty("lat", circle.lat)
                addProperty("lng", circle.lng)
                addProperty("radius", circle.radius)
            }
            revealCirclesJsonArray.add(revealCircle)
        }

        return JsonObject().apply {
            add("revealCircles", revealCirclesJsonArray)
        }
    }

    fun sendJsonToServer(path: String, jsonData: JsonObject){
        val body = jsonData.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(SERVER_URL + path)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException){
                e.printStackTrace()
                Log.e("NetworkUtil", "request fail: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    Log.d("NetworkUtil", "call success: ${response.body?.string()}")
                } else{
                    Log.e("NetworkUtil", "call fail: ${response.code}")
                }
            }
        })

    }

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
