package com.dudoji.android.presentation.follow.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.domain.model.User

@RequiresApi(Build.VERSION_CODES.O)
class FollowAdapter(
    private val onFollowClick: (User) -> Unit,
) : ListAdapter<User, FollowAdapter.FollowViewHolder>(UserDiffCallback) {

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
        if (getItem(position).followingAt != null) {
            return VIEW_TYPE_FOLLOWED
        }
        return VIEW_TYPE_UNFOLLOWED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val layoutId = when (viewType) {
            VIEW_TYPE_FOLLOWED -> R.layout.follow_followed_user_item
            VIEW_TYPE_UNFOLLOWED -> R.layout.follow_unfollowed_user_item
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return FollowViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        val user = getItem(position)

        holder.name.text = user.name
        holder.email.text = user.email
        holder.image.load(user.profileImageUrl) {
            crossfade(true)
            error(R.drawable.dudoji_profile)
            placeholder(R.drawable.dudoji_profile)
        }

        holder.actionButton.setOnClickListener {
            onFollowClick.invoke(user)
        }
    }

    object UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}