package com.dudoji.android.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.dudoji.android.map.MapActivity
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.mypage.achievement.AchievementActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MypageActivity : NavigatableActivity() {

    override val navigationItems = mapOf(
        R.id.mypageFragment to null,
        R.id.mapFragment to MapActivity::class.java
    )



    private lateinit var bottomNav: BottomNavigationView

    // 기본 선택 항목 설정
    override val defaultSelectedItemId = R.id.mypageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        bottomNav = findViewById(R.id.navigationView)
        bottomNav.selectedItemId = R.id.mypageFragment

        setupBottomNavigation(findViewById(R.id.navigationView))


        val statisticsView = findViewById<TextView>(R.id.statisticsView)
        statisticsView.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        val achievementView = findViewById<TextView>(R.id.achievementView)
        achievementView.setOnClickListener {
            val intent = Intent(this, AchievementActivity::class.java)
            startActivity(intent)

        }
    }
}
