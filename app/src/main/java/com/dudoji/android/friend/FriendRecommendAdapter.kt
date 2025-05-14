package com.dudoji.android.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.friend.domain.User
import com.dudoji.android.map.activity.MapActivity

class FriendRecommendAdapter(val recommendedFriends: List<User>, val activity: MapActivity) : RecyclerView.Adapter<FriendRecommendAdapter.FriendRecommendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRecommendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_recommended, parent, false)
        return FriendRecommendViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FriendRecommendViewHolder,
        position: Int
    ) {
        val user = recommendedFriends[position]

        holder.name.text = user.name
        holder.email.text = user.email
        val imageUrl = user.profileImageUrl
        if (imageUrl.isNotEmpty()) {
            Glide.with(activity)
                .load(imageUrl)
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .into(holder.image)
        }

        holder.itemView.setOnClickListener {
            suspend {
                RetrofitClient.friendApiService.addFriend(user.id)
                Toast.makeText(activity, "${user.name}에게 친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = recommendedFriends.size

    inner class FriendRecommendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val email: TextView = view.findViewById(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.imgProfile)
    }
}