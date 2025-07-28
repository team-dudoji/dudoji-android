package com.dudoji.android.follow.adapter

import android.os.Build
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
    private val users: List<User>,
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
            .inflate(R.layout.following_item, parent, false)
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

        activity.lifecycleScope.launch {
            val isFollowing = FollowRepository.getFollowings().any { it.id == user.id }
            updateFollowButtonState(holder.followButton, isFollowing)
        }

        holder.followButton.setOnClickListener {
            activity.lifecycleScope.launch {
                val isNowFollowing = FollowRepository.getFollowings().any { it.id == user.id }

                val result = if (isNowFollowing)
                    FollowRepository.deleteFollowing(user)
                else
                    FollowRepository.addFollowing(user)

                if (result) {
                    val msg = if (isNowFollowing) "언팔로우" else "팔로우"
                    Toast.makeText(activity, "${user.name} $msg 완료", Toast.LENGTH_SHORT).show()
                    updateFollowButtonState(holder.followButton, !isNowFollowing)
                } else {
                    Toast.makeText(activity, "요청 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = users.size

    private fun updateFollowButtonState(button: Button, isFollowing: Boolean) {
        if (isFollowing) {
            button.setBackgroundResource(R.drawable.following_button)
            button.setTextColor(ContextCompat.getColor(button.context, android.R.color.white))
        } else {
            button.setBackgroundResource(R.drawable.follow_button)
            button.setTextColor(ContextCompat.getColor(button.context, android.R.color.black))
        }
    }
}
