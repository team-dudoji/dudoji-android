package com.dudoji.android.pin.util

import android.os.Build
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.pin.adapter.PinMemoAdapter
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

            val skinRed = view.findViewById<ImageView>(R.id.skin_red)
            val skinOrange = view.findViewById<ImageView>(R.id.skin_orange)
            val skinBlue = view.findViewById<ImageView>(R.id.skin_blue)
            val saveButton = view.findViewById<Button>(R.id.skin_save_button)
            var selectedSkin = pin.pinSkin.ifEmpty { "pin_orange" } //기본 주황색

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
            
            //선택된 pin은 검은색 테투리
            fun updateSelectedSkinUI(selected: String) {
                val selectedBorder = R.drawable.selected_border
                skinRed.setBackgroundResource(if (selected == "pin_red") selectedBorder else 0)
                skinOrange.setBackgroundResource(if (selected == "pin_orange") selectedBorder else 0)
                skinBlue.setBackgroundResource(if (selected == "pin_blue") selectedBorder else 0)

            }

            updateSelectedSkinUI(selectedSkin)

            skinRed.setOnClickListener {
                selectedSkin = "pin_red"
                updateSelectedSkinUI(selectedSkin)
            }

            skinOrange.setOnClickListener {
                selectedSkin = "pin_orange"
                updateSelectedSkinUI(selectedSkin)
            }

            skinBlue.setOnClickListener {
                selectedSkin = "pin_blue"
                updateSelectedSkinUI(selectedSkin)
            }

            //선택한 핀 맵에 적용 후 서버로 전송
            saveButton.setOnClickListener {
                if (selectedSkin != pin.pinSkin) {
                    // pinList에서 해당 핀을 찾은 후 색상 갱신
                    val targetPin = PinRepository.pinList.find { it.pinId == pin.pinId }
                    targetPin?.pinSkin = selectedSkin
                    //마커 초기화 후 다시 갱신
                    clusterManager.clearItems()
                    PinRepository.pinList.forEach { clusterManager.addItem(it) }
                    clusterManager.cluster()
                    //서버에 보냄
                    activity.lifecycleScope.launch {
                        PinRepository.updatePinSkin(pin.pinId, selectedSkin)
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
