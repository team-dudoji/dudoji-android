package com.dudoji.android.mypage.activity

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.follow.FollowAdapter
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch

class FollowerListActivity : AppCompatActivity() {

    private lateinit var followerRecyclerView: RecyclerView
    private lateinit var followerAdapter: FollowAdapter
    private val followersList = mutableListOf<User>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follower_list)

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "팔로워 목록"

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        followerRecyclerView = findViewById(R.id.follower_recycler_view)
        followerRecyclerView.layoutManager = LinearLayoutManager(this)

        followerAdapter = FollowAdapter(followersList, this)
        followerRecyclerView.adapter = followerAdapter

        loadFollowers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadFollowers() {
        lifecycleScope.launch {
            try {
                val fetchedFollowers = FollowRepository.getFollowers()
                followersList.clear()
                followersList.addAll(fetchedFollowers)
                followerAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@FollowerListActivity, "팔로워 목록을 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}