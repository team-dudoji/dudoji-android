package com.dudoji.android.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.map.repository.PinRepository

class FriendAdapter(private val friends: List<Friend>, private val activity: MapActivity) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.textName)
        val desc = view.findViewById<TextView>(R.id.textDesc)
        val visibilityIcon = view.findViewById<ImageView>(R.id.imgVisibility)
        val image: ImageView = view.findViewById(R.id.imgProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]

        holder.name.text = friend.user.name
        holder.desc.text = friend.user.email
        val imageUrl = friend.user.profileImageUrl
        if (imageUrl.isNotEmpty()) {
            Glide.with(activity)
                .load(imageUrl)
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .into(holder.image)
        }
        holder.visibilityIcon.setImageResource(
            if (friend.isVisible) R.drawable.visibility else R.drawable.visibility_off
        )

        holder.visibilityIcon.setOnClickListener {
            friend.isVisible = !friend.isVisible
            PinRepository.updateFilter(activity.pinSetterController.pinApplier)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = friends.size
}
