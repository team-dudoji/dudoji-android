package com.dudoji.android.mypage.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dudoji.android.NavigatableActivity
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class MypageActivity : NavigatableActivity() {
    private val TAG = "MypageActivityDEBUG"

    override val navigationItems = mapOf(
        R.id.mypageFragment to null,
        R.id.mapFragment to MapActivity::class.java
    )

    private lateinit var bottomNav: BottomNavigationView

    // 기본 선택 항목 설정
    override val defaultSelectedItemId = R.id.mypageFragment

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        bottomNav = findViewById(R.id.navigationView)
        bottomNav.selectedItemId = R.id.mypageFragment

        setupBottomNavigation(findViewById(R.id.navigationView))

        setProfile()

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setProfile() {
        val name = findViewById<TextView>(R.id.name)
        val profileImage = findViewById<ShapeableImageView>(R.id.profile_image)

        lifecycleScope.launch{
            val userNameResponse = RetrofitClient.userApiService.getUserName()
            val userProfileImageResponse = RetrofitClient.userApiService.getUserProfileImageUrl()
            Log.d(TAG, "name response: ${userNameResponse.body()}")
            if (userNameResponse.isSuccessful) {
                val nameText = userNameResponse.body()
                if (nameText != null) {
                    name.setText(nameText)
                }
            }
            Log.d(TAG, "image response: ${userProfileImageResponse.body()}")
            if (userProfileImageResponse.isSuccessful) {
                val imageUrl = userProfileImageResponse.body()
                if (imageUrl != null) {
                    Glide.with(this@MypageActivity)
                        .load(imageUrl)
                        .error(R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .into(profileImage)
                }
            }
        }
    }
}
