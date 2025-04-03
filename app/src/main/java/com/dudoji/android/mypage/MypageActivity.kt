package com.dudoji.android.mypage

import android.content.Intent
import android.os.Bundle
import com.dudoji.android.map.MapActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MypageActivity : NavigatableActivity() {

    override val navigationItems = mapOf(
        R.id.mypageFragment to null,
        R.id.mapFragment to MapActivity::class.java
    )

    // 기본 선택 항목 설정
    override val defaultSelectedItemId = R.id.mypageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val bottomNav = findViewById<BottomNavigationView>(R.id.navigationView)

        bottomNav.selectedItemId = defaultSelectedItemId

        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            val targetActivity = navigationItems[menuItem.itemId]
            if (targetActivity != null) {
                bottomNav.selectedItemId = menuItem.itemId

                if (targetActivity == MapActivity::class.java) {
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                }
                return@setOnNavigationItemSelectedListener true
            }
            false
        }
    }
}
