package com.dudoji.android.presentation.follow

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityFollowListBinding
import com.dudoji.android.domain.model.SortType
import com.dudoji.android.domain.model.User
import com.dudoji.android.domain.model.UserType
import com.dudoji.android.presentation.follow.adapter.FollowAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class FollowListActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "type"
    }

    private lateinit var binding: ActivityFollowListBinding

    private val followViewModel: FollowViewModel by viewModels()

    private lateinit var followAdapter: FollowAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeString = intent.getStringExtra(EXTRA_TYPE) ?: "NONE"
        followViewModel.type = UserType.valueOf(typeString)

        binding.sortButton.load("file:///android_asset/follow/ic_sort_up_down.png")
        binding.personAddIcon.load("file:///android_asset/follow/person_add.png")

        binding.followerSection.setOnClickListener { selectSection(UserType.FOLLOWER) }
        binding.followingSection.setOnClickListener { selectSection(UserType.FOLLOWING) }
        binding.friendAddSection.setOnClickListener { selectSection(UserType.NONE) }

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        
        binding.followListRecyclerView.layoutManager = LinearLayoutManager(this)
        followAdapter = FollowAdapter(userList, { onUserClick(it) })
        binding.followListRecyclerView.adapter = followAdapter

        binding.searchSection.searchEditText.doOnTextChanged {
            text, _, _, _ ->
            followViewModel.keyword = text.toString()
            this.loadUsers()
            this.loadCounts()
            true
        }

        binding.sortButton.setOnClickListener { v -> showSortPopup(v) }

        selectSection(followViewModel.type)
        loadCounts()
    }

    private fun showSortPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)

        popup.menuInflater.inflate(R.menu.follow_sort_menu, popup.menu)
        popup.menu.setGroupCheckable(R.id.group_sort, true, true)

        // 현재 정렬 상태 체크 표시
        val checkedId = when (followViewModel.sort) {
            SortType.NAME_ASC -> R.id.sort_name
            SortType.NEWEST   -> R.id.sort_latest
            SortType.OLDEST   -> R.id.sort_oldest
            else              -> R.id.sort_name
        }
        popup.menu.findItem(checkedId).isChecked = true

        popup.setOnMenuItemClickListener { item ->
            item.isChecked = true
            followViewModel.sort = when (item.itemId) {
                R.id.sort_name   -> SortType.NAME_ASC
                R.id.sort_latest -> SortType.NEWEST
                R.id.sort_oldest -> SortType.OLDEST
                else             -> followViewModel.sort
            }
            loadUsers()
            true
        }
        popup.show()
    }

    private fun selectSection(sectionType: UserType) {
        followViewModel.type = sectionType
        loadUsers()
    }

    private fun onUserClick(user: User) {
        lifecycleScope.launch {
            followViewModel.toggleFollow(user)
            loadUsers()
            loadCounts()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadUsers() {
        lifecycleScope.launch {
            val users = when (followViewModel.type) {
                UserType.FOLLOWER ->
                    followViewModel.getUsers(
                        UserType.FOLLOWER,
                        0,
                        20,
                        SortType.NEWEST,
                        ""
                    )

                UserType.FOLLOWING -> followViewModel.getUsers(
                    UserType.FOLLOWING,
                    0,
                    20,
                    SortType.NEWEST,
                    ""
                )
                else -> followViewModel.getUsers(
                    UserType.NONE,
                    0,
                    20,
                    SortType.NEWEST,
                    binding.searchSection.searchEditText.text.toString()
                )
            }

            userList.clear()
            userList.addAll(users)
            followAdapter.notifyDataSetChanged()
        }
    }

    private fun loadCounts() {
        lifecycleScope.launch {
            try {
                binding.followersCount.text = followViewModel.getFollowersNum().toString()
                binding.followingCount.text = followViewModel.getFollowingsNum().toString()
            } catch (e: Exception) {
                binding.followersCount.text = "0"
                binding.followingCount.text = "0"
                Toast.makeText(this@FollowListActivity, "수 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}