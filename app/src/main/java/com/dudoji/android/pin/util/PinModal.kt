package com.dudoji.android.pin.util

import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.pin.adapter.PinMemoAdapter
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.fragment.PinMemoInputFragment
import com.dudoji.android.util.modal.Modal
import kotlinx.coroutines.launch
import java.io.IOException

object PinModal {

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinMemoModal(activity: AppCompatActivity, pin: Pin) {
        Modal.showCustomModal(activity, R.layout.show_pin_memo_modal) { view ->
            view.findViewById<ImageView>(R.id.memo_date_edit_button).load("file:///android_asset/pin/calendar_today.png")
            view.findViewById<ImageView>(R.id.location_icon).load("file:///android_asset/pin/location_on.png")

            val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
            val pinImage = view.findViewById<ImageView>(R.id.pin_memo_image)
            val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
            val pinLikeButton = view.findViewById<ImageView>(R.id.memo_like_button)
            val pinLikeCount = view.findViewById<TextView>(R.id.memo_like_count)
            val pinPlaceName = view.findViewById<TextView>(R.id.pin_place_name)
            val pinAddress = view.findViewById<TextView>(R.id.pin_address)
            var isLiked = pin.isLiked

            val placeholderDrawable = try {
                activity.assets.open("pin/photo_placeholder.png").use { Drawable.createFromStream(it, null) }
            } catch (e: IOException) { null }

            pinImage.load("${RetrofitClient.BASE_URL}/${pin.imageUrl}") {
                crossfade(true)
                error(placeholderDrawable)
                placeholder(placeholderDrawable)
            }

            fun updateLikeButton() {
                val heartIconPath = if (isLiked) "pin/heart_like.png" else "pin/heart_unlike.png"
                pinLikeButton.load("file:///android_asset/$heartIconPath")
            }
            updateLikeButton()

            pinLikeCount.text = pin.likeCount.toString()
            pinContent.text = pin.content
            pinDate.text = pin.createdDate.toString()
            pinPlaceName.text = pin.placeName.ifEmpty { "장소 정보 없음" }
            pinAddress.text = pin.address.ifEmpty { "주소 정보 없음" }

            pinLikeButton.setOnClickListener {
                isLiked = !isLiked
                if (isLiked) {
                    pin.likeCount += 1
                    activity.lifecycleScope.launch { RetrofitClient.pinApiService.likePin(pin.pinId) }
                } else {
                    pin.likeCount -= 1
                    activity.lifecycleScope.launch { RetrofitClient.pinApiService.unlikePin(pin.pinId) }
                }
                pinLikeCount.text = pin.likeCount.toString()
                updateLikeButton() // 아이콘 업데이트
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
        Modal.showCustomModal(activity, PinMemoInputFragment(lat, lng, activity,  onComplete))
    }
}