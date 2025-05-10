package com.dudoji.android.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

class FriendAdapter(private val friends: List<Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.textName)
        val desc = view.findViewById<TextView>(R.id.textDesc)
        val visibilityIcon = view.findViewById<ImageView>(R.id.imgVisibility)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]

        holder.name.text = friend.name
        holder.desc.text = friend.desc
        holder.visibilityIcon.setImageResource(
            if (friend.isVisible) R.drawable.visibility else R.drawable.visibility_off
        )

        holder.visibilityIcon.setOnClickListener {
            friend.isVisible = !friend.isVisible  // 상태 토글
            notifyItemChanged(position)           // UI 갱신
        }

        holder.visibilityIcon.setOnClickListener {
            friend.isVisible = !friend.isVisible
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = friends.size
}
