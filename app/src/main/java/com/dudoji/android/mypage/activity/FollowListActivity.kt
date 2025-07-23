package com.dudoji.android.mypage.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.follow.FollowAdapter
import com.dudoji.android.follow.activity.FriendAddActivity
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch

class FollowListActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TYPE = "type"
        const val TYPE_FOLLOWER = "follower"
        const val TYPE_FOLLOWING = "following"
    }

    private lateinit var followAdapter: FollowAdapter
    private val userList = mutableListOf<User>()

    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
    private lateinit var followerSection: View
    private lateinit var followingSection: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_list) // 기존 레이아웃 그대로 사용

        val type = intent.getStringExtra(EXTRA_TYPE)
        val titleView = findViewById<TextView>(R.id.toolbar_title)
        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val recyclerView = findViewById<RecyclerView>(R.id.follow_list_recycler_view)
        val friendAddSection = findViewById<LinearLayout>(R.id.friend_add_section)

        followersCount = findViewById(R.id.followers_count)
        followingCount = findViewById(R.id.following_count)
        followerSection = findViewById(R.id.follower_section)
        followingSection = findViewById(R.id.following_section)

        followerSection.setOnClickListener {
            titleView.text = "팔로워 목록"
            loadUsers(TYPE_FOLLOWER)
        }

        followingSection.setOnClickListener {
            titleView.text = "팔로잉 목록"
            loadUsers(TYPE_FOLLOWING)
        }

        titleView.text = if (type == TYPE_FOLLOWING) "팔로잉 목록" else "팔로워 목록"
        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


        recyclerView.layoutManager = LinearLayoutManager(this)
        followAdapter = FollowAdapter(userList, this)
        recyclerView.adapter = followAdapter

        loadUsers(type)

        loadCounts()

        friendAddSection.setOnClickListener {
            startActivity(Intent(this, FriendAddActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadUsers(type: String?) {
        lifecycleScope.launch {
            try {
                val users = if (type == TYPE_FOLLOWING)
                    FollowRepository.getFollowings()
                else
                    FollowRepository.getFollowers()

                userList.clear()
                userList.addAll(users)
                followAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@FollowListActivity, "불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCounts() {
        lifecycleScope.launch {
            try {
                val followers = FollowRepository.getFollowers()
                val followings = FollowRepository.getFollowings()

                followersCount.text = followers.size.toString()
                followingCount.text = followings.size.toString()
            } catch (e: Exception) {
                Toast.makeText(this@FollowListActivity, "수 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
