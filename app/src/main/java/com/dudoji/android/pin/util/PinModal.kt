package com.dudoji.android.pin.util

import RetrofitClient
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageButton
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

@RequiresApi(Build.VERSION_CODES.O)
object PinModal {

    private data class Ui(
        val userAvatar: ShapeableImageView,
        val followButton: ImageButton,
        val pinPlaceName: TextView,
        val pinContent: TextView,
        val pinImage: ImageView,
        val pinDate: TextView,
        val pinLikeButton: ImageView,
        val pinLikeCount: TextView,
        val hashtagRv: RecyclerView
    )

    fun openPinMemoModal(activity: AppCompatActivity, pin: Pin) {
        Modal.showCustomModal(activity, R.layout.show_pin_memo_modal) { view ->
            val ui = bindViews(view, activity)
            bindBasicPinInfo(activity, ui, pin)
            setupLikeSection(activity, ui, pin)
            setupFollowSection(activity, ui, pin)
        }
    }

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

    fun openPinDataModal(activity: MapActivity, lat: Double, lng: Double, onComplete: (PinMakeData) -> Unit) {
        Modal.showCustomModal(activity, PinMemoInputFragment(lat, lng, activity, onComplete))
    }


    private fun bindViews(view: View, activity: AppCompatActivity): Ui {
        view.findViewById<ImageView>(R.id.memo_date_edit_button)
            .loadAsset("pin/calendar_today.png")

        val hashtagRv = view.findViewById<RecyclerView>(R.id.hashtag_recycler_view).apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        return Ui(
            userAvatar = view.findViewById(R.id.user_avatar),
            followButton = view.findViewById(R.id.follow_button),
            pinPlaceName = view.findViewById(R.id.pin_place_name),
            pinContent = view.findViewById(R.id.memo_content_output),
            pinImage = view.findViewById(R.id.pin_memo_image),
            pinDate = view.findViewById(R.id.memo_date_output),
            pinLikeButton = view.findViewById(R.id.memo_like_button),
            pinLikeCount = view.findViewById(R.id.memo_like_count),
            hashtagRv = hashtagRv
        )
    }

    private fun bindBasicPinInfo(
        activity: AppCompatActivity,
        ui: Ui,
        pin: Pin
    ) {
        val placeholder = activity.loadAssetDrawable("pin/photo_placeholder.png")
        ui.pinImage.load("${RetrofitClient.BASE_URL}/${pin.imageUrl}") {
            crossfade(true)
            error(placeholder)
            placeholder(placeholder)
        }
        ui.hashtagRv.adapter = HashtagAdapter(pin.hashtags.toMutableList())
        ui.pinLikeCount.text = pin.likeCount.toString()
        ui.pinContent.text = pin.content
        ui.pinDate.text = pin.createdDate.toString()
        ui.pinPlaceName.text = pin.placeName.ifEmpty { "장소 정보 없음" }
    }

    private fun setupLikeSection(
        activity: AppCompatActivity,
        ui: Ui,
        pin: Pin
    ) {
        var isLiked = pin.isLiked

        fun render() {
            ui.pinLikeButton.loadAsset(
                if (isLiked) "pin/heart_like.png" else "pin/heart_unlike.png"
            )
            ui.pinLikeCount.text = pin.likeCount.toString()
        }

        render()

        ui.pinLikeButton.setOnClickListener {
            isLiked = !isLiked
            pin.likeCount += if (isLiked) 1 else -1
            render()

            activity.lifecycleScope.launch {
                try {
                    if (isLiked) {
                        RetrofitClient.pinApiService.likePin(pin.pinId)
                    } else {
                        RetrofitClient.pinApiService.unlikePin(pin.pinId)
                    }
                } catch (e: Exception) {
                    // Rollback
                    isLiked = !isLiked
                    pin.likeCount += if (isLiked) 1 else -1
                    render()
                }
            }
        }
    }


    private fun setupFollowSection(
        activity: AppCompatActivity,
        ui: Ui,
        pin: Pin
    ) {
        when (pin.master) {
            Who.MINE -> {
                ui.followButton.visibility = View.GONE
                loadMyAvatar(activity, ui)
            }
            Who.FOLLOWING, Who.UNKNOWN -> {
                ui.followButton.visibility = View.VISIBLE
                activity.lifecycleScope.launch {
                    runCatching {
                        val resp = RetrofitClient.userApiService.getUserProfile(pin.userId)
                        if (!resp.isSuccessful) throw IOException("Failed getUserProfile: code=${resp.code()}")
                        resp.body() ?: throw IOException("User profile body is null")
                    }.onSuccess { dto ->
                        ui.userAvatar.load(dto.profileImageUrl) {
                            placeholder(R.drawable.dudoji_profile)
                            error(R.drawable.dudoji_profile)
                        }
                        setupFollowToggle(activity, ui.followButton, pin, dto.name)
                    }.onFailure { e ->
                        Log.e("PinModal", "FOLLOWING/UNKNOWN: profile load error", e)
                        ui.followButton.visibility = View.GONE
                        ui.userAvatar.load(R.drawable.dudoji_profile)
                    }
                }
            }
        }
    }

    private fun loadMyAvatar(activity: AppCompatActivity, ui: Ui) {
        activity.lifecycleScope.launch {
            runCatching { MyPageRemoteDataSource.getUserProfile() }
                .onSuccess { me ->
                    ui.userAvatar.load(me?.profileImageUrl) {
                        placeholder(R.drawable.dudoji_profile)
                        error(R.drawable.dudoji_profile)
                    }
                }
                .onFailure { e ->
                    Log.e("PinModal", "My profile fetch failed", e)
                    ui.userAvatar.load(R.drawable.dudoji_profile)
                }
        }
    }

    private fun setupFollowToggle(
        activity: AppCompatActivity,
        button: ImageButton,
        pin: Pin,
        targetName: String
    ) {
        var isFollowing = (pin.master == Who.FOLLOWING)

        fun render() {
            button.background = activity.loadAssetDrawable(
                if (isFollowing) "follow/following_button.png" else "follow/follow_button.png"
            )
            button.backgroundTintList = null
        }

        render()

        button.setOnClickListener {
            val prev = isFollowing
            isFollowing = !isFollowing
            render()

            activity.lifecycleScope.launch {
                val ok = if (isFollowing) {
                    RetrofitClient.followApiService.addFriend(pin.userId).isSuccessful
                } else {
                    RetrofitClient.followApiService.deleteFriend(pin.userId).isSuccessful
                }

                if (ok) {
                    val msg = if (isFollowing) {
                        if (targetName == "사용자") "팔로우했습니다." else "${targetName}님을 팔로우했습니다."
                    } else {
                        if (targetName == "사용자") "언팔로우했습니다." else "${targetName}님을 언팔로우했습니다."
                    }
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                } else {
                    isFollowing = prev
                    render()
                    Toast.makeText(activity, "요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun AppCompatActivity.loadAssetDrawable(path: String): Drawable? =
        try {
            assets.open(path).use { Drawable.createFromStream(it, null) }
        } catch (_: IOException) {
            null
        }

    private fun ImageView.loadAsset(path: String) {
        this.load("file:///android_asset/$path")
    }
}
