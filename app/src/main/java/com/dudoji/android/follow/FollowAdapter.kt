package com.dudoji.android.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.follow.domain.User
import com.dudoji.android.map.activity.MapActivity

class FollowAdapter(private val followings: List<User>, private val activity: MapActivity) :
    RecyclerView.Adapter<FollowAdapter.FollowingViewHolder>() {

    inner class FollowingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.textName)
        val desc = view.findViewById<TextView>(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.imgProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_following, parent, false)
        return FollowingViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        val following = followings[position]

        holder.name.text = following.name
        holder.desc.text = following.email
        val imageUrl = following.profileImageUrl
        if (!imageUrl.isNullOrBlank()) {
            Glide.with(activity)
                .load(imageUrl)
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .into(holder.image)
        }
    }

    override fun getItemCount() = followings.size
}
