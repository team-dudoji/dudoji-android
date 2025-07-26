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

@RequiresApi(Build.VERSION_CODES.O)
class FollowAdapter(
    private val users: List<User>,
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<FollowAdapter.FollowViewHolder>() {

    inner class FollowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val email: TextView = view.findViewById(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.following_item_image)
        val actionButton: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deletable_following_item, parent, false)
        return FollowViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        val user = users[position]

        holder.name.text = user.name
        holder.email.text = user.email
        holder.image.load(user.profileImageUrl) {
            crossfade(true)
            error(R.drawable.user_placeholder)
            placeholder(R.drawable.user_placeholder)
        }

        activity.lifecycleScope.launch {
            val followings = FollowRepository.getFollowings()
            val isFollowing = followings.any { it.id == user.id }

            updateFollowButtonState(holder.actionButton, isFollowing)

            holder.actionButton.setOnClickListener {
                handleFollowAction(user, holder.actionButton)
            }
        }
    }

    private fun handleFollowAction(user: User, button: Button) {
        activity.lifecycleScope.launch {
            val isFollowingNow = FollowRepository.getFollowings().any { it.id == user.id }

            val result: Boolean
            if (isFollowingNow) {
                result = FollowRepository.deleteFollowing(user)
                if (result) {
                    Toast.makeText(activity, "${user.name}님을 언팔로우했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                result = FollowRepository.addFollowing(user)
                if (result) {
                    Toast.makeText(activity, "${user.name}님을 팔로우했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            if (result) {
                updateFollowButtonState(button, !isFollowingNow)
            } else {
                Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFollowButtonState(button: Button, isFollowing: Boolean) {
        if (isFollowing) {
            button.setBackgroundResource(R.drawable.following_button)
        } else {
            button.setBackgroundResource(R.drawable.follow_button)
        }
        button.backgroundTintList = null
    }

    override fun getItemCount(): Int = users.size
}