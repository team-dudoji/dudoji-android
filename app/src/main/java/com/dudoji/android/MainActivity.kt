package com.dudoji.android

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dudoji.android.map.MapActivity
import com.dudoji.android.util.RequestPermissionsUtil
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class   MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onStart() {
        super.onStart()
        RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //시작 종료 버튼
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            val serviceIntent = Intent(this, MyService::class.java)
            startService(serviceIntent)
            Toast.makeText(this, "로그 시작", Toast.LENGTH_SHORT).show()
        }

        btnStop.setOnClickListener {
            val serviceIntent = Intent(this, MyService::class.java)
            stopService(serviceIntent)
            Toast.makeText(this, "로그 종료", Toast.LENGTH_SHORT).show()
        }

        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // MapActivity 이동 버튼 설정
        setButtonMapping()

        // 위치 정보 관련 뷰 설정
        val locationText: TextView = findViewById(R.id.locationText)
        val locationButton: Button = findViewById(R.id.locationButton)
        locationButton.setOnClickListener {
            getLocation(locationText)
        }
    }

    // MapActivity로 이동하는 버튼 매핑
    private fun setButtonMapping() {
        val toMapButton = findViewById<Button>(R.id.toMapButton)
        toMapButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapActivity::class.java))
        }
    }



    //위도, 경도, 주소값을 가져옴
    @SuppressLint("MissingPermission")
    private fun getLocation(textView: TextView) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude

                    val address = getAddress(latitude, longitude)?.firstOrNull()

                    textView.text = """
                    위도: ${"%.6f".format(latitude)}
                    경도: ${"%.6f".format(longitude)}
                    
                    주소: ${address?.getAddressLine(0) ?: "주소를 확인할 수 없음"}
                """.trimIndent()
                } ?: run {
                    textView.text = "위치 정보를 가져올 수 없습니다"
                }
            }
            .addOnFailureListener { e ->
                textView.text = "에러: ${e.localizedMessage}"
            }
    }

    //주소 가져와버렸
    private fun getAddress(lat: Double, lng: Double): List<Address>? {
        return try {
            Geocoder(this, Locale.KOREA).getFromLocation(lat, lng, 1)
        } catch (e: IOException) {
            Toast.makeText(this, "주소를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show()
            null
        }
    }
}