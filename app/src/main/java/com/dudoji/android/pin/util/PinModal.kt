package com.dudoji.android.pin.util

import android.os.Build
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.pin.adapter.PinColorAdapter
import com.dudoji.android.pin.adapter.PinMemoAdapter
import com.dudoji.android.pin.color.PinColor
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.fragment.PinMemoInputFragment
import com.dudoji.android.pin.repository.PinRepository
import com.dudoji.android.util.modal.Modal
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.launch

object PinModal {
    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinMemoModal(activity: AppCompatActivity, pin: Pin, clusterManager: ClusterManager<Pin>) {
        Modal.showCustomModal(activity, R.layout.show_pin_memo_modal) { view ->
            val pinContent = view.findViewById<TextView>(R.id.memo_content_output)
            val pinImage = view.findViewById<ImageView>(R.id.pin_memo_image)
            val pinDate = view.findViewById<TextView>(R.id.memo_date_output)
            val pinLikeButton = view.findViewById<ImageView>(R.id.memo_like_button)
            val pinLikeCount = view.findViewById<TextView>(R.id.memo_like_count)
            val pinPlaceName = view.findViewById<TextView>(R.id.pin_place_name)
            val pinAddress = view.findViewById<TextView>(R.id.pin_address)
            val isLiked = pin.isLiked
            val pinColorRecyclerView = view.findViewById<RecyclerView>(R.id.pin_color_recycler_view)
            val saveButton = view.findViewById<Button>(R.id.skin_save_button)

            val pinColors = listOf(
                PinColor(1, "빨강", R.drawable.pin_red),
                PinColor(2, "주황", R.drawable.pin_orange),
                PinColor(3, "파랑", R.drawable.pin_blue),
            )

            val initialSelectedPinColorId = when (pin.pinSkin) {
                "pin_red" -> R.drawable.pin_red
                "pin_orange" -> R.drawable.pin_orange
                "pin_blue" -> R.drawable.pin_blue
                else -> R.drawable.pin_orange // 기본값
            }

            var selectedPinColor: PinColor? = pinColors.find { it.imageResId == initialSelectedPinColorId }
            selectedPinColor = selectedPinColor ?: pinColors.find { it.id == 2 } // 못찾으면 주황색으로 기본값 설정 (ID 2는 주황)

            val pinColorAdapter = PinColorAdapter(pinColors) { selected ->
                selectedPinColor = selected
            }

            pinColorRecyclerView.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                adapter = pinColorAdapter
            }

            selectedPinColor?.let {
                pinColorAdapter.setSelectedColor(it.id)
            }

            Glide.with(activity)
                .load("${RetrofitClient.BASE_URL}${pin.imageUrl}")
                .placeholder(R.drawable.photo_placeholder)
                .into(pinImage)

            pinLikeButton.setImageDrawable(
                activity.getDrawable(
                    if (isLiked) R.drawable.heart_like
                    else R.drawable.heart_unlike))

            pinLikeCount.text = pin.likeCount.toString()
            pinContent.text = pin.content
            pinDate.text = pin.createdDate.toString()
            pinPlaceName.text = pin.placeName.ifEmpty { "장소 정보 없음" }
            pinAddress.text = pin.address.ifEmpty { "주소 정보 없음" }

            pinLikeButton.setOnClickListener {
                if (isLiked) {
                    pinLikeButton.setImageDrawable(activity.getDrawable(R.drawable.heart_unlike))
                    pinLikeCount.text = (pin.likeCount - 1).toString()
                    pin.likeCount -= 1
                    pin.isLiked = false
                    activity.lifecycleScope.launch {
                        RetrofitClient.pinApiService.unlikePin(pin.pinId)
                    }
                } else {
                    pinLikeButton.setImageDrawable(activity.getDrawable(R.drawable.heart_like))
                    pinLikeCount.text = (pin.likeCount + 1).toString()
                    pin.likeCount += 1
                    pin.isLiked = true
                    activity.lifecycleScope.launch {
                        RetrofitClient.pinApiService.likePin(pin.pinId)
                    }
                }
            }


            //선택한 핀 맵에 적용 후 서버로 전송
            saveButton.setOnClickListener {
                val newSelectedSkinName = selectedPinColor?.let {
                    activity.resources.getResourceEntryName(it.imageResId)
                }

                if (newSelectedSkinName != null && newSelectedSkinName != pin.pinSkin) {
                    val targetPin = PinRepository.pinList.find { it.pinId == pin.pinId }
                    targetPin?.pinSkin = newSelectedSkinName
                    //마커 초기화 후 다시 갱신
                    clusterManager.clearItems()
                    PinRepository.pinList.forEach { clusterManager.addItem(it) }
                    clusterManager.cluster()
                    //서버에 보냄
                    activity.lifecycleScope.launch {
                        PinRepository.updatePinSkin(pin.pinId, newSelectedSkinName)
                        Toast.makeText(
                            activity,
                            "${selectedPinColor?.name} 핀으로 변경되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun openPinMemosModal(activity: AppCompatActivity, pins: List<Pin>,  clusterManager: ClusterManager<Pin>) {
        Modal.showCustomModal(activity, R.layout.show_pin_memos_modal) { view ->
            val memos = view.findViewById<RecyclerView>(R.id.memos_recycler_view)
            memos.layoutManager = LinearLayoutManager(activity)
            val memoAdapter = PinMemoAdapter(pins.toList())
            memos.adapter = memoAdapter
            val touchListener = object : RecyclerView.OnItemTouchListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val childView = rv.findChildViewUnder(e.x, e.y)
                    if (childView != null && e.action == MotionEvent.ACTION_UP) {
                        val position = rv.getChildAdapterPosition(childView)
                        val pin = pins.elementAt(position)
                        openPinMemoModal(activity, pin, clusterManager)
                        return true
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            }

            memos.addOnItemTouchListener(touchListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openPinDataModal(activity: MapActivity, lat: Double, lng: Double, onComplete: (PinMakeData) -> Unit) {
        Modal.showCustomModal(activity, PinMemoInputFragment(lat, lng, onComplete))
    }
}
