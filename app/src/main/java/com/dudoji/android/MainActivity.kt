package com.dudoji.android

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
import com.dudoji.android.databinding.ActivityMainBinding
import com.dudoji.android.map.MapActivity
import com.dudoji.android.util.RequestPermissionsUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class   MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
        RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 바인딩 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNav = findViewById(R.id.navigationView)
        setupBottomNavigation()

        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // 맵 화면 유지
                    true
                }
                R.id.mapFragment -> {
                    startActivity(Intent(this, MapActivity::class.java)) // MapActivity로 이동
                    true
                }
                else -> false
            }
        }
    }
}
