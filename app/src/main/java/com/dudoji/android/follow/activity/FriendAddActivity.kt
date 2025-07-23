package com.dudoji.android.follow.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.follow.FollowSearchAdapter
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch

class FriendAddActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FollowSearchAdapter

        private val recommandUsers = mutableListOf<User>()
    private val filteredUsers = mutableListOf<User>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_add)

        searchInput = findViewById(R.id.friend_search_edit_text)
        recyclerView = findViewById(R.id.friend_recommend_recycler_view)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = FollowSearchAdapter(filteredUsers, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotBlank()) {
                    fetchRecommendedUsers(query)
                } else {
                    recommandUsers.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun fetchRecommendedUsers(email: String) {
        lifecycleScope.launch {
            try {
                val users = FollowRepository.getRecommendedUsers(email)
                recommandUsers.clear()
                recommandUsers.addAll(users)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
            }
        }
    }

}
