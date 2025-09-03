package com.dudoji.android.presentation.follow

import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityFollowListBinding
import com.dudoji.android.domain.model.User
import com.dudoji.android.domain.repository.FollowRepository
import com.dudoji.android.domain.repository.FollowRepository.UserType
import com.dudoji.android.presentation.follow.adapter.FollowAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class FollowListActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "type"
        const val TYPE_FOLLOWER = "follower"
        const val TYPE_FOLLOWING = "following"
    }

    private val binding: ActivityFollowListBinding by lazy {
        ActivityFollowListBinding.inflate(layoutInflater)
    }
    private val followViewModel: FollowViewModel by viewModels()

    private lateinit var followAdapter: FollowAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_list)

        val type = intent.getStringExtra(EXTRA_TYPE)
        
        val friendAddSection = findViewById<LinearLayout>(R.id.friend_add_section)

        binding.sortButton.load("file:///android_asset/follow/ic_sort_up_down.png")
        binding.personAddIcon.load("file:///android_asset/follow/person_add.png")

        binding.followerSection.setOnClickListener {
            binding.toolbarTitle.text = "팔로워 목록"
            loadUsers(TYPE_FOLLOWER)
        }

        binding.followingSection.setOnClickListener {
            binding.toolbarTitle.text = "팔로잉 목록"
            loadUsers(TYPE_FOLLOWING)
        }

        binding.toolbarTitle.text = if (type == TYPE_FOLLOWING) "팔로잉 목록" else "팔로워 목록"
        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        
        binding.followListRecyclerView.layoutManager = LinearLayoutManager(this)
        followAdapter = FollowAdapter(userList, { onUserClick(it) })
        binding.followListRecyclerView.adapter = followAdapter

        loadUsers(type)
        loadCounts()
    }

    private fun onUserClick(user: User) {
        // 유저 클릭 시 동작 구현 (예: 유저 프로필로 이동)
        Toast.makeText(this, "${user.name} 클릭됨", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadUsers(type: String?) {
        lifecycleScope.launch {
            try {
                val users =
                    if (type == TYPE_FOLLOWING)
                        followViewModel.getUsers(
                            UserType.FOLLOWING,
                            0,
                            20,
                            FollowRepository.SortType.NEWEST,
                            ""
                        )
                    else
                        followViewModel.getUsers(
                            UserType.FOLLOWER,
                            0,
                            20,
                            FollowRepository.SortType.NEWEST,
                            ""
                        )

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
                binding.followersCount.text = followViewModel.getFollowersNum.toString()
                binding.followingCount.text = followViewModel.getFollowingsNum.toString()
            } catch (e: Exception) {
                binding.followersCount.text = "0"
                binding.followingCount.text = "0"
                Toast.makeText(this@FollowListActivity, "수 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}