package com.dudoji.android.mypage.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch

class FollowingListActivity : AppCompatActivity() {

    private lateinit var followingRecyclerView: RecyclerView
    private lateinit var followingAdapter: FollowAdapter
    private val followingsList = mutableListOf<User>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "팔로잉 목록"
        
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        followingRecyclerView = findViewById(R.id.following_recycler_view)
        followingRecyclerView.layoutManager = LinearLayoutManager(this)

        followingAdapter = FollowAdapter(followingsList, this) // 액티비티 컨텍스트 전달
        followingRecyclerView.adapter = followingAdapter

        loadFollowings()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadFollowings() {
        lifecycleScope.launch {
            try {
                val fetchedFollowings = FollowRepository.getFollowings()
                followingsList.clear()
                followingsList.addAll(fetchedFollowings)
                followingAdapter.notifyDataSetChanged() 

            } catch (e: Exception) {
                Toast.makeText(this@FollowingListActivity, "팔로잉 목록 불러오기 실패.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}