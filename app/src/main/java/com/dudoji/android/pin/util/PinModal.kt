package com.dudoji.android.pin.util

import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.follow.domain.User
import com.dudoji.android.follow.repository.FollowRepository
import com.dudoji.android.landmark.adapter.HashtagAdapter
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.mypage.repository.MyPageRemoteDataSource
import com.dudoji.android.pin.adapter.PinMemoAdapter
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who
import com.dudoji.android.pin.fragment.PinMemoInputFragment
import com.dudoji.android.util.modal.Modal
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch
import java.io.IOException

object PinModal {

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinMemoModal(activity: AppCompatActivity, pin: Pin) {
        Modal.showCustomModal(activity, R.layout.show_pin_memo_modal) { view ->
            val userAvatar = view.findViewById<ShapeableImageView>(R.id.user_avatar)
            val followButton = view.findViewById<Button>(R.id.follow_button)
            val pinPlaceName = view.findViewById<TextView>(R.id.pin_place_name)
            val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
            val pinImage = view.findViewById<ImageView>(R.id.pin_memo_image)
            val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
            val pinLikeButton = view.findViewById<ImageView>(R.id.memo_like_button)
            val pinLikeCount = view.findViewById<TextView>(R.id.memo_like_count)
            val hashtagRecyclerView = view.findViewById<RecyclerView>(R.id.hashtag_recycler_view)
            view.findViewById<ImageView>(R.id.memo_date_edit_button).load("file:///android_asset/pin/calendar_today.png")

            var isLiked = pin.isLiked
            var isFollowing = (pin.master == Who.FOLLOWING)

            val placeholderDrawable = try {
                activity.assets.open("pin/photo_placeholder.png").use { Drawable.createFromStream(it, null) }
            } catch (e: IOException) { null }

            pinImage.load("${RetrofitClient.BASE_URL}/${pin.imageUrl}") {
                crossfade(true)
                error(placeholderDrawable)
                placeholder(placeholderDrawable)
            }

            hashtagRecyclerView.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            hashtagRecyclerView.adapter = HashtagAdapter(pin.hashtags.toMutableList())

            pinLikeCount.text = pin.likeCount.toString()
            pinContent.text = pin.content
            pinDate.text = pin.createdDate.toString()
            pinPlaceName.text = pin.placeName.ifEmpty { "장소 정보 없음" }

            fun updateLikeButton() {
                val heartIconPath = if (isLiked) "pin/heart_like.png" else "pin/heart_unlike.png"
                pinLikeButton.load("file:///android_asset/$heartIconPath")
            }

            fun updateFollowButton(button: Button, followingState: Boolean) {
                try {
                    val backgroundPath = if (followingState) "follow/following_button.png" else "follow/follow_button.png"
                    val drawable = activity.assets.open(backgroundPath).use { Drawable.createFromStream(it, null) }
                    button.background = drawable
                } catch (e: IOException) { e.printStackTrace() }
                button.backgroundTintList = null
            }

            updateLikeButton()

            pinLikeButton.setOnClickListener {

                isLiked = !isLiked
                pin.likeCount += if (isLiked) 1 else -1
                updateLikeButton()
                pinLikeCount.text = pin.likeCount.toString()

                activity.lifecycleScope.launch {
                    try {
                        if (isLiked) RetrofitClient.pinApiService.likePin(pin.pinId)
                        else RetrofitClient.pinApiService.unlikePin(pin.pinId)
                    } catch (e: Exception) {
                        isLiked = !isLiked
                        pin.likeCount += if (isLiked) 1 else -1
                        updateLikeButton()
                        pinLikeCount.text = pin.likeCount.toString()
                    }
                }
            }

            activity.lifecycleScope.launch {
                when (pin.master) {
                    Who.MINE -> {
                        followButton.visibility = View.GONE
                        try {
                            val myProfile = MyPageRemoteDataSource.getUserProfile()
                            myProfile?.let {
                                userAvatar.load(it.profileImageUrl) {
                                    placeholder(R.drawable.dudoji_profile)
                                    error(R.drawable.dudoji_profile)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PinModal", "Failed to fetch my profile for my pin", e)
                            userAvatar.load(R.drawable.dudoji_profile)
                        }
                    }

                    Who.FOLLOWING, Who.UNKNOWN -> {
                        followButton.visibility = View.VISIBLE
                        updateFollowButton(followButton, isFollowing)

                        try {
                            val response = RetrofitClient.userApiService.getUserProfile(pin.userId)

                            if (!response.isSuccessful) {
                                throw IOException("Failed to get user profile: ${response.code()}")
                            }

                            val incorrectUserProfileDto = response.body() ?: throw IOException("User profile body is null")

                            val targetUser = User(
                                id = pin.userId,
                                name = incorrectUserProfileDto.name,
                                email = incorrectUserProfileDto.email,
                                profileImageUrl = incorrectUserProfileDto.profileImageUrl,
                                password = "", role = "", createAt = java.util.Date(), provider = "", providerId = ""
                            )

                            userAvatar.load(targetUser.profileImageUrl) {
                                placeholder(R.drawable.dudoji_profile)
                                error(R.drawable.dudoji_profile)
                            }

                            followButton.setOnClickListener {
                                activity.lifecycleScope.launch {
                                    val originalFollowingState = isFollowing
                                    isFollowing = !isFollowing
                                    updateFollowButton(followButton, isFollowing)

                                    val result = if (isFollowing) FollowRepository.addFollowing(targetUser) else FollowRepository.deleteFollowing(targetUser)

                                    if (result) {
                                        val message: String
                                        if (isFollowing) {
                                            message = if (targetUser.name == "사용자") "팔로우했습니다." else "${targetUser.name}님을 팔로우했습니다."
                                            FollowRepository.getFollowings()
                                        } else {
                                            message = if (targetUser.name == "사용자") "언팔로우했습니다." else "${targetUser.name}님을 언팔로우했습니다."
                                            FollowRepository.getFollowings() 
                                        }
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        isFollowing = originalFollowingState
                                        updateFollowButton(followButton, isFollowing)
                                        Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PinModal", "Error in FOLLOWING/UNKNOWN case", e)
                            followButton.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinMemosModal(activity: AppCompatActivity, pins: List<Pin>) {
        Modal.showCustomModal(activity, R.layout.show_pin_memos_modal) { view ->
            val memos = view.findViewById<RecyclerView>(R.id.memos_recycler_view)
            memos.layoutManager = LinearLayoutManager(activity)
            val memoAdapter = PinMemoAdapter(pins.toList()) { pin ->
                openPinMemoModal(activity, pin)
            }
            memos.adapter = memoAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinDataModal(activity: MapActivity, lat: Double, lng: Double, onComplete: (PinMakeData) -> Unit) {
        Modal.showCustomModal(activity, PinMemoInputFragment(lat, lng, activity, onComplete))
    }
}