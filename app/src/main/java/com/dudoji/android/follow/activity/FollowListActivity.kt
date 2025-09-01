package com.dudoji.android.follow.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.follow.adapter.FollowAdapter
import com.dudoji.android.follow.adapter.FollowSortType
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

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

    private var currentType: String = TYPE_FOLLOWER
    private var currentSort: FollowSortType = FollowSortType.NAME_KOR

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_list)

        val type = intent.getStringExtra(EXTRA_TYPE)
        currentType = if (type == TYPE_FOLLOWING) TYPE_FOLLOWING else TYPE_FOLLOWER

        val titleView = findViewById<TextView>(R.id.toolbar_title)
        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val recyclerView = findViewById<RecyclerView>(R.id.follow_list_recycler_view)
        val friendAddSection = findViewById<LinearLayout>(R.id.friend_add_section)
        val sortButton = findViewById<ImageButton>(R.id.sort_button)
        val personAddIcon = findViewById<ImageView>(R.id.person_add_icon)

        sortButton.load("file:///android_asset/follow/ic_sort_up_down.png")
        personAddIcon.load("file:///android_asset/follow/person_add.png")

        followersCount = findViewById(R.id.followers_count)
        followingCount = findViewById(R.id.following_count)
        followerSection = findViewById(R.id.follower_section)
        followingSection = findViewById(R.id.following_section)

        followerSection.setOnClickListener {
            titleView.text = "팔로워 목록"
            currentType = TYPE_FOLLOWER
            currentSort = FollowSortType.NAME_KOR
            loadUsers(currentType)
        }

        followingSection.setOnClickListener {
            titleView.text = "팔로잉 목록"
            currentType = TYPE_FOLLOWING
            currentSort = FollowSortType.NAME_KOR
            loadUsers(currentType)
        }

        titleView.text = if (currentType == TYPE_FOLLOWING) "팔로잉 목록" else "팔로워 목록"
        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        followAdapter = FollowAdapter(
            users = userList,
            activity = this
        )
        recyclerView.adapter = followAdapter

        sortButton.setOnClickListener { v -> showSortPopup(v) }

        // 초기 데이터 & 카운트 로드
        loadUsers(currentType)
        loadCounts()

        friendAddSection.setOnClickListener {
            startActivity(Intent(this, FriendAddActivity::class.java))
        }
    }

    private fun showSortPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        // XML 메뉴 inflate
        popup.menuInflater.inflate(R.menu.menu_follow_sort, popup.menu)
        // 그룹을 단일 선택으로 (체크가 이동하는 느낌)
        popup.menu.setGroupCheckable(R.id.group_sort, true, true)

        // 현재 정렬 상태 체크 표시
        val checkedId = when (currentSort) {
            FollowSortType.NAME_KOR -> R.id.sort_name
            FollowSortType.LATEST   -> R.id.sort_latest
            FollowSortType.OLDEST   -> R.id.sort_oldest
        }
        popup.menu.findItem(checkedId).isChecked = true

        popup.setOnMenuItemClickListener { item ->
            item.isChecked = true
            currentSort = when (item.itemId) {
                R.id.sort_name   -> FollowSortType.NAME_KOR
                R.id.sort_latest -> FollowSortType.LATEST
                R.id.sort_oldest -> FollowSortType.OLDEST
                else             -> currentSort
            }
            applySortAndRefresh()
            true
        }
        popup.show()
    }

    // 정렬 적용 + 리스트 새로고침
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applySortAndRefresh() {
        sortUsersInPlace(userList, currentSort, currentType)
        followAdapter.notifyDataSetChanged()
    }

    // 정렬 로직
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortUsersInPlace(
        list: MutableList<User>,
        sortType: FollowSortType,
        type: String
    ) {
        val collator = Collator.getInstance(Locale.KOREAN).apply {
            strength = Collator.PRIMARY // 한국어 사전식 정렬
        }

        when (sortType) {
            FollowSortType.NAME_KOR -> {
                list.sortWith { u1, u2 -> collator.compare(u1.name, u2.name) }
            }
            FollowSortType.LATEST -> {
                list.sortWith { u1, u2 ->
                    val t1 = if (type == TYPE_FOLLOWER) u1.followAt else u1.followedAt
                    val t2 = if (type == TYPE_FOLLOWER) u2.followAt else u2.followedAt
                    when {
                        t1 == null && t2 == null -> collator.compare(u1.name, u2.name)
                        t1 == null -> 1
                        t2 == null -> -1
                        else -> t2.compareTo(t1) // 최신순(내림차순)
                    }
                }
            }
            FollowSortType.OLDEST -> {
                list.sortWith { u1, u2 ->
                    val t1 = if (type == TYPE_FOLLOWER) u1.followAt else u1.followedAt
                    val t2 = if (type == TYPE_FOLLOWER) u2.followAt else u2.followedAt
                    when {
                        t1 == null && t2 == null -> collator.compare(u1.name, u2.name)
                        t1 == null -> 1
                        t2 == null -> -1
                        else -> t1.compareTo(t2) // 오래된순(오름차순)
                    }
                }
            }
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
                applySortAndRefresh()
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
