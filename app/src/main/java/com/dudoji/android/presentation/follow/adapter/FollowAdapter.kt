package com.dudoji.android.presentation.follow.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.domain.model.User

@RequiresApi(Build.VERSION_CODES.O)
class FollowAdapter(
    private val users: List<User>,
    private val onFollowClick: (User) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FOLLOWED = 1
        private const val VIEW_TYPE_UNFOLLOWED = 2
    }

    inner class FollowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val email: TextView = view.findViewById(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.following_item_image)
        val actionButton: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun getItemViewType(position: Int): Int {
        if (users[position].followedAt != null) {
            return VIEW_TYPE_FOLLOWED
        }
        return VIEW_TYPE_UNFOLLOWED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        when (viewType) {
            VIEW_TYPE_FOLLOWED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.follow_followed_user_item, parent, false)
                return FollowViewHolder(view)
            }
            VIEW_TYPE_UNFOLLOWED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.follow_unfollowed_user_item, parent, false)
                return FollowViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = users[position]

        if (holder !is FollowViewHolder) return

        holder.name.text = user.name
        holder.email.text = user.email
        holder.image.load(user.profileImageUrl) {
            crossfade(true)
            error(R.drawable.dudoji_profile)
            placeholder(R.drawable.dudoji_profile)
        }

        holder.actionButton.setOnClickListener {
            onFollowClick.invoke(user)
            notifyItemRangeChanged(position, 1)
        }
    }

//    private fun handleFollowAction(user: User, button: Button) {
//        val isFollowingNow = user.followingAt != null
//
//        val result: Boolean
//        if (isFollowingNow) {
//            result = FollowRepository.deleteFollowing(user)
//            if (result) {
//                Toast.makeText(activity, "${user.name}님을 언팔로우했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            result = FollowRepository.addFollowing(user)
//            if (result) {
//                Toast.makeText(activity, "${user.name}님을 팔로우했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        if (result) {
//            updateFollowButtonState(button, !isFollowingNow)
//        } else {
//            Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun getItemCount(): Int = users.size
}