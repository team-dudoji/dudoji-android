package com.dudoji.android.presentation.follow

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
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
        const val UNSELECTED_COLOR = "#666666"
        const val SELECTED_COLOR = "#FF8445"
    }

    private lateinit var binding: ActivityFollowListBinding

    private val followViewModel: FollowViewModel by viewModels()

    private lateinit var followAdapter: FollowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeString = intent.getStringExtra(EXTRA_TYPE) ?: "NONE"
        followViewModel.loadInitialData(UserType.valueOf(typeString))

        binding.sortButton.load("file:///android_asset/follow/ic_sort_up_down.png")
        binding.personAddIcon.load("file:///android_asset/follow/person_add.png")

        binding.followerSection.setOnClickListener { followViewModel.setListType(UserType.FOLLOWER) }
        binding.followingSection.setOnClickListener { followViewModel.setListType(UserType.FOLLOWING) }
        binding.noneSection.setOnClickListener { followViewModel.setListType(UserType.NONE) }

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        
        binding.followListRecyclerView.layoutManager = LinearLayoutManager(this)
        followAdapter = FollowAdapter({ onUserClick(it) })
        binding.followListRecyclerView.adapter = followAdapter

        binding.searchSection.searchEditText.doOnTextChanged {
            text, _, _, _ ->
            followViewModel.setSearchKeyword(text.toString())
            true
        }

        binding.sortButton.setOnClickListener { v -> showSortPopup(v) }

        lifecycleScope.launch {
            followViewModel.uiState.collect { state ->
                selectSection(state.currentType)
                followAdapter.submitList(state.users)
                binding.followingCount.text = state.followingCount.toString()
                binding.followersCount.text = state.followerCount.toString()
            }
        }
    }

    private fun showSortPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)

        popup.menuInflater.inflate(R.menu.follow_sort_menu, popup.menu)
        popup.menu.setGroupCheckable(R.id.group_sort, true, true)

        val checkedId = when (followViewModel.uiState.value.sort) {
            SortType.NAME_ASC -> R.id.sort_name
            SortType.NEWEST   -> R.id.sort_latest
            SortType.OLDEST   -> R.id.sort_oldest
            else              -> R.id.sort_name
        }
        popup.menu.findItem(checkedId).isChecked = true

        popup.setOnMenuItemClickListener { item ->
            item.isChecked = true
            val newSortType = when (item.itemId) {
                R.id.sort_name   -> SortType.NAME_ASC
                R.id.sort_latest -> SortType.NEWEST
                R.id.sort_oldest -> SortType.OLDEST
                else             -> followViewModel.uiState.value.sort
            }
            followViewModel.setSortType(newSortType)
            true
        }
        popup.show()
    }

    private fun selectSection(sectionType: UserType) {
        when (sectionType) {
            UserType.FOLLOWER -> {
                setSelectSection(UserType.FOLLOWER, true)
                setSelectSection(UserType.FOLLOWING, false)
                setSelectSection(UserType.NONE, false)
            }
            UserType.FOLLOWING -> {
                setSelectSection(UserType.FOLLOWER, false)
                setSelectSection(UserType.FOLLOWING, true)
                setSelectSection(UserType.NONE, false)
            }
            UserType.NONE -> {
                setSelectSection(UserType.FOLLOWER, false)
                setSelectSection(UserType.FOLLOWING, false)
                setSelectSection(UserType.NONE, true)
            }
        }
    }

    private fun setSelectSection(sectionType: UserType, selected: Boolean) {
        val visibility = if (selected) View.VISIBLE else View.INVISIBLE
        val color = Color.parseColor(if (selected) SELECTED_COLOR else UNSELECTED_COLOR)

        when(sectionType) {
            UserType.FOLLOWER -> {
                binding.followerUnderBar.visibility = visibility
                binding.followerText.setTextColor(color)
                binding.followersCount.setTextColor(color)
            }
            UserType.FOLLOWING -> {
                binding.followingUnderBar.visibility = visibility
                binding.followingText.setTextColor(color)
                binding.followingCount.setTextColor(color)
            }
            UserType.NONE -> {
                binding.noneUnderBar.visibility = visibility
                binding.noneText.setTextColor(color)
                binding.personAddIcon.setColorFilter(color)
            }
        }
    }

    private fun onUserClick(user: User) {
        lifecycleScope.launch {
            followViewModel.toggleFollow(user)
        }
    }
}