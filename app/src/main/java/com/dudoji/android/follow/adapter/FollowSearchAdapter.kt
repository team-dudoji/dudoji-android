package com.dudoji.android.follow.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import kotlinx.coroutines.launch

class FollowSearchAdapter(
    private var users: List<User>,
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<FollowSearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val email: TextView = view.findViewById(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.following_item_image)
        val followButton: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deletable_following_item, parent, false)
        return SearchViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]

        holder.name.text = user.name
        holder.email.text = user.email
        holder.image.load(user.profileImageUrl) {
            crossfade(true)
            error(R.drawable.user_placeholder)
            placeholder(R.drawable.user_placeholder)
        }

        val imageUrl = user.profileImageUrl
        Log.d("skr", "유저: ${user.name}, 이미지 URL: $imageUrl")

        activity.lifecycleScope.launch {
            val isFollowing = FollowRepository.getFollowings().any { it.id == user.id }
            updateFollowButtonState(holder.followButton, isFollowing)
        }

        holder.followButton.setOnClickListener {
            activity.lifecycleScope.launch {
                val isNowFollowing = FollowRepository.getFollowings().any { it.id == user.id }

                val result = if (isNowFollowing) {
                    FollowRepository.deleteFollowing(user)
                } else {
                    FollowRepository.addFollowing(user)
                }

                if (result) {
                    val msg = if (isNowFollowing) "언팔로우" else "팔로우"
                    Toast.makeText(activity, "${user.name} 님을 ${msg}했습니다.", Toast.LENGTH_SHORT).show()
                    updateFollowButtonState(holder.followButton, !isNowFollowing)
                } else {
                    Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUserList(newUsers: List<User>) {
        this.users = newUsers
        newUsers.forEach { user ->
            Log.d("skrskr", "사용자: ${user.name}, 프로필 이미지 URL: ${user.profileImageUrl}")
        }
        notifyDataSetChanged()
    }

    private fun updateFollowButtonState(button: Button, isFollowing: Boolean) {
        if (isFollowing) {
            button.setBackgroundResource(R.drawable.following_button)
        } else {
            button.setBackgroundResource(R.drawable.follow_button)
        }
        button.backgroundTintList = null
    }
}