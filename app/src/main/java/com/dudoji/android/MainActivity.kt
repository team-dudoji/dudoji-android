package com.dudoji.android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dudoji.android.databinding.ActivityMainBinding
import com.dudoji.android.map.MapActivity
import com.dudoji.android.util.RequestPermissionsUtil

class MainActivity : NavigatableActivity() {

    private lateinit var binding: ActivityMainBinding

    override val navigationItems = mapOf(
        R.id.homeFragment to null, // 홈 메인
        R.id.mapFragment to MapActivity::class.java,
//        R.id.locationFragment to LocationActivity::class.java
    )

    override val defaultSelectedItemId = R.id.homeFragment

    override fun onStart() {
        super.onStart()
        RequestPermissionsUtil(this).requestLocation() // 위치 권한 요청
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.navigationView)

        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



}
