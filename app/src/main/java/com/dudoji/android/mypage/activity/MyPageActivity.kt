package com.dudoji.android.mypage.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.follow.activity.FollowListActivity
import com.dudoji.android.mypage.adapter.AchievementAdapter
import com.dudoji.android.mypage.adapter.DailyQuestAdapter
import com.dudoji.android.mypage.adapter.LandmarkAdapter
import com.dudoji.android.mypage.repository.MyPageRemoteDataSource
import com.dudoji.android.mypage.domain.QuestType
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
        val settingsButton = findViewById<ImageButton>(R.id.settings_button)

        val followerSection = findViewById<LinearLayout>(R.id.follower_section_clickable)
        val followingSection = findViewById<LinearLayout>(R.id.following_section_clickable)

        settingsButton.load("file:///android_asset/account/ic_settings.png")

        dailyQuestRecycler = findViewById(R.id.daily_quest_recycler)
        landmarkRecycler = findViewById(R.id.landmark_recycler)
        achievementRecycler = findViewById(R.id.achievement_recycler)

        dailyQuestRecycler.layoutManager = LinearLayoutManager(this)
        landmarkRecycler.layoutManager = LinearLayoutManager(this)
        achievementRecycler.layoutManager = GridLayoutManager(this, 3)

        lifecycleScope.launch {
            try {
                val userProfile = MyPageRemoteDataSource.getUserProfile()
                userProfile?.let { profile ->
                    nameTv.text = profile.name
                    email.text = profile.email
                    pinCount.text = profile.pinCount.toString()
                    followerCount.text = profile.followerCount.toString()
                    followingCount.text = profile.followingCount.toString()

                    profileImage.load(profile.profileImageUrl) {
                        crossfade(true)
                        error(R.drawable.dudoji_profile)
                        placeholder(R.drawable.dudoji_profile)
                    }
                }

                val quests = MyPageRemoteDataSource.getQuests()
                dailyQuestAdapter = DailyQuestAdapter(
                    quests.stream().filter({ it.questType == QuestType.DAILY }).toList()
                )
                dailyQuestRecycler.adapter = dailyQuestAdapter

                landmarkAdapter = LandmarkAdapter(
                    quests.stream().filter({ it.questType == QuestType.LANDMARK }).toList()
                )
                landmarkRecycler.adapter = landmarkAdapter

                val achievements = MyPageRemoteDataSource.getAchievements()
                achievementAdapter = AchievementAdapter(achievements)
                achievementRecycler.adapter = achievementAdapter

            } catch (e: Exception) {
                Log.e(TAG, "오류 발생: ${e.message}", e)
            }

            followerSection.setOnClickListener {
                val intent = Intent(this@MyPageActivity, FollowListActivity::class.java)
                intent.putExtra(FollowListActivity.EXTRA_TYPE, FollowListActivity.TYPE_FOLLOWER)
                startActivity(intent)
            }

            followingSection.setOnClickListener {
                Log.d("MyPageDEBUG", "팔로워 섹션 클릭됨")
                val intent = Intent(this@MyPageActivity, FollowListActivity::class.java)
                intent.putExtra(FollowListActivity.EXTRA_TYPE, FollowListActivity.TYPE_FOLLOWING)
                startActivity(intent)
            }


            settingsButton.setOnClickListener {
                Log.d("MyPageDEBUG", "팔로잉 섹션 클릭됨")
                val intent = Intent(this@MyPageActivity, AccountManageActivity::class.java)
                startActivity(intent)
            }
        }
    }
}