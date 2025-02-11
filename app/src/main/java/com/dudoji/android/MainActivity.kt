package com.dudoji.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dudoji.android.map.MapActivity
import com.dudoji.android.util.RequestPermissionsUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) // 새 Intent를 업데이트합니다.
        setupBottomNavigation() // 네비게이션 상태를 다시 설정합니다.
    }


    override fun onStart() {
        super.onStart()
        RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
                    // 홈 화면 유지
                    true
                }
                R.id.mapFragment -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }


}