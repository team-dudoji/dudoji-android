package com.dudoji.android.util

import android.util.Log
import com.dudoji.android.config.SERVER_URL
import com.dudoji.android.repository.RevealCircleRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class NetWorkUtil {
    private val client = OkHttpClient()


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
}
