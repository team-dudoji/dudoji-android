package com.dudoji.android.follow.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.follow.adapter.FollowSearchAdapter
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class FriendAddActivity : AppCompatActivity() {

    private lateinit var searchAdapter: FollowSearchAdapter
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var emailIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_add)

        searchEditText = findViewById(R.id.friend_search_edit_text)
        recyclerView = findViewById(R.id.friend_recommend_recycler_view)
        backButton = findViewById(R.id.back_button)
        emailIcon = findViewById(R.id.email_icon)

        emailIcon.load("file:///android_asset/follow/email.png")

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        searchAdapter = FollowSearchAdapter(emptyList(), this)
        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("skr", "검색 버튼 클릭됨!") // 디버깅 로그

                val email = textView.text.toString().trim()
                if (email.isNotBlank()) {
                    searchUserByEmail(email)
                }
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun searchUserByEmail(email: String) {
        lifecycleScope.launch {
            try {
                val userList = FollowRepository.getRecommendedUsers(email)
                searchAdapter.updateUserList(userList)
            } catch (e: Exception) {
            }
        }
    }
}