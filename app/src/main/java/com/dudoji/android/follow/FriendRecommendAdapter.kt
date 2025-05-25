package com.dudoji.android.follow

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import com.dudoji.android.map.activity.MapActivity
import kotlinx.coroutines.launch

class FriendRecommendAdapter(val recommendedFriends: List<User>, val activity: MapActivity) : RecyclerView.Adapter<FriendRecommendAdapter.FriendRecommendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRecommendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.following_item, parent, false)
        return FriendRecommendViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: FriendRecommendViewHolder,
        position: Int
    ) {
        val user = recommendedFriends[position]

        holder.name.text = user.name
        holder.email.text = user.email
        val imageUrl = user.profileImageUrl
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(activity)
                .load(imageUrl)
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .into(holder.image)
        }

        holder.itemView.setOnClickListener {
            activity.lifecycleScope.launch {
                if (FollowRepository.addFollowing(user))
                    Toast.makeText(activity, "${user.name}를 팔로우 합니다.", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity, "팔로우에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = recommendedFriends.size

    inner class FriendRecommendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val email: TextView = view.findViewById(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.following_item_image)
    }
}