package com.dudoji.android.mypage.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.mypage.adapter.AchievementAdapter
import com.dudoji.android.mypage.adapter.DailyQuestAdapter
import com.dudoji.android.mypage.adapter.LandmarkAdapter
import com.dudoji.android.mypage.repository.MyPageRepository
import com.dudoji.android.mypage.type.QuestType
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class MyPageActivity : AppCompatActivity() {
    private val TAG = "MypageActivityDEBUG"
    private lateinit var dailyQuestRecycler: RecyclerView
    private lateinit var landmarkRecycler: RecyclerView
    private lateinit var achievementRecycler: RecyclerView
    private lateinit var dailyQuestAdapter: DailyQuestAdapter
    private lateinit var landmarkAdapter: LandmarkAdapter
    private lateinit var achievementAdapter: AchievementAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        loadMypageData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMypageData() {
        val email = findViewById<TextView>(R.id.user_email)
        val profileImage = findViewById<ShapeableImageView>(R.id.profile_image)
        val nameTv = findViewById<TextView>(R.id.name)
        val pinCount = findViewById<TextView>(R.id.pin_count)
        val followerCount = findViewById<TextView>(R.id.follower_count)
        val followingCount = findViewById<TextView>(R.id.following_count)

        dailyQuestRecycler = findViewById(R.id.daily_quest_recycler)
        landmarkRecycler = findViewById(R.id.landmark_recycler)
        achievementRecycler = findViewById(R.id.achievement_recycler)

        dailyQuestRecycler.layoutManager = LinearLayoutManager(this)
        landmarkRecycler.layoutManager = LinearLayoutManager(this)
        achievementRecycler.layoutManager = GridLayoutManager(this, 3)

        lifecycleScope.launch {
            try {
                val userProfile = MyPageRepository.getUserProfile()
                userProfile?.let { profile ->
                    nameTv.text = profile.name
                    email.text = profile.email
                    pinCount.text = profile.pinCount.toString()
                    followerCount.text = profile.followerCount.toString()
                    followingCount.text = profile.followingCount.toString()

                    Glide.with(this@MyPageActivity)
                        .load(profile.profileImageUrl)
                        .error(R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .into(profileImage)
                }

                val quests = MyPageRepository.getDailyQuests()
                dailyQuestAdapter = DailyQuestAdapter(
                    quests.stream().filter({ it.questType == QuestType.DAILY }).toList()
                )
                dailyQuestRecycler.adapter = dailyQuestAdapter

                landmarkAdapter = LandmarkAdapter(
                    quests.stream().filter({ it.questType == QuestType.LANDMARK }).toList()
                )
                landmarkRecycler.adapter = landmarkAdapter

                val achievements = MyPageRepository.getAchievements()
                achievementAdapter = AchievementAdapter(achievements)
                achievementRecycler.adapter = achievementAdapter

            } catch (e: Exception) {
                Log.e(TAG, "오류 발생: ${e.message}", e)
            }
        }
    }
}