package com.dudoji.android.follow

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

class FollowAdapter(private val followings: List<User>, private val activity: AppCompatActivity) :
    RecyclerView.Adapter<FollowAdapter.FollowingViewHolder>() {

    inner class FollowingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.textName)
        val desc = view.findViewById<TextView>(R.id.textEmail)
        val image: ImageView = view.findViewById(R.id.following_item_image)
        val deleteButton = view.findViewById<Button>(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.deletable_following_item, parent, false)
        return FollowingViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        val following = followings[position]
        holder.name.text = following.name
        holder.desc.text = following.email
        var isFollowing = true
        val imageUrl = following.profileImageUrl
        if (!imageUrl.isNullOrBlank()) {
            holder.image.load(imageUrl) {
                crossfade(true)
                error(R.drawable.user_placeholder)
                placeholder(R.drawable.user_placeholder)
            }
        }
        holder.deleteButton.setOnClickListener {
            activity.lifecycleScope.launch {
                if (!isFollowing) {
                    FollowRepository.addFollowing(following)
                    Toast.makeText(activity, "${following.name}를 팔로우 합니다.", Toast.LENGTH_SHORT).show()
                    holder.deleteButton.text = "팔로우"
                    holder.deleteButton.setBackgroundColor(activity.getColor(R.color.orange))
                    isFollowing = true
                } else {
                    if (FollowRepository.deleteFollowing(following)){
                        Toast.makeText(activity, "${following.name}를 언팔로우 합니다.", Toast.LENGTH_SHORT).show()
                        holder.deleteButton.text = "팔로잉"
                        holder.deleteButton.setBackgroundColor(activity.getColor(R.color.orange))
                        isFollowing = false
                    } else
                        Toast.makeText(activity, "언팔로우에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                updateFollowButtonState(holder.deleteButton, isFollowing)
                holder.deleteButton.text = if (isFollowing) "팔로잉" else "팔로우"
            }
        }
    }

    override fun getItemCount() = followings.size

    //버튼 상태에 따른 이미지 변경
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
