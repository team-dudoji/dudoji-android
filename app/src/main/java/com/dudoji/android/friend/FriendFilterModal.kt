package com.dudoji.android.friend

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.friend.domain.User
import com.dudoji.android.friend.repository.FriendRepository
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.util.modal.Modal
import kotlinx.coroutines.launch

object FriendModal {
    fun openFriendFilterModal(activity: MapActivity) {
        Modal.showCustomModal(activity, R.layout.friend_modal) { view ->
            val closeBtn = view.findViewById<ImageView>(R.id.btnCloseModal)
            closeBtn.setOnClickListener {
                (view.parent?.parent?.parent as? ViewGroup)?.removeView(view.parent.parent as View)
            }

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFriendList)
            recyclerView.layoutManager = LinearLayoutManager(activity)

            val friends = FriendRepository.getFriends()
            val addFriendButton = view.findViewById<ImageView>(R.id.btnAddFriend)
            addFriendButton.setOnClickListener {
                openFriendFinderModal(activity)
            }
            recyclerView.adapter = FriendAdapter(friends, activity)
        }
    }

    fun openFriendFinderModal(activity: MapActivity) {
        Modal.showCustomModal(activity, R.layout.find_friend_modal) { view ->
            val closeBtn = view.findViewById<ImageView>(R.id.btnCloseModal)
            closeBtn.setOnClickListener {
                (view.parent?.parent?.parent as? ViewGroup)?.removeView(view.parent.parent as View)
            }

            val recyclerView = view.findViewById<RecyclerView>(R.id.friend_recommend_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(activity)

            val friends: MutableList<User> = mutableListOf()

            val emailEditText = view.findViewById<EditText>(R.id.friend_recommend_edit_text)
            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val email = s.toString()
                    if (email.isNotEmpty()) {
                        activity.lifecycleScope.launch {
                            try {
                                friends.clear()
                                friends.addAll(FriendRepository.getRecommendedFriends(email))
                                recyclerView.adapter?.notifyDataSetChanged()
                            } catch (e: Exception) {
                                friends.clear()
                                recyclerView.adapter?.notifyDataSetChanged()
                                Log.e("FriendRecommendation", "API 오류: ${e.message}")
                            }
                        }
                    } else {
                        friends.clear()
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })


            recyclerView.adapter = FriendRecommendAdapter(friends, activity)
        }
    }
}