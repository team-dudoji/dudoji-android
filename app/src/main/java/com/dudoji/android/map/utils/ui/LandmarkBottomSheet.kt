package com.dudoji.android.map.utils.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.pin.activity.PinDetailActivity
import com.dudoji.android.pin.adapter.PinMemoAdapter
import com.dudoji.android.pin.adapter.SortType
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.IOException

class LandmarkBottomSheet(val landmarkBottomSheet: LinearLayout,
                          private val context: Context) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val landmarkDetailImageView: ImageView by lazy {landmarkBottomSheet.findViewById<ImageView>(R.id.landmark_detail_image)}
    private val landmarkTitleTextView: TextView by lazy {landmarkBottomSheet.findViewById<TextView>(R.id.landmark_name)}
    private val landmarkDescriptionTextView: TextView by lazy {landmarkBottomSheet.findViewById<TextView>(R.id.landmark_description)}
    private val landmarkRecyclerView: RecyclerView by lazy {landmarkBottomSheet.findViewById<RecyclerView>(R.id.landmark_recycler_view)}
    private val pinMemoAdapter: PinMemoAdapter by lazy {
        PinMemoAdapter(emptyList(), ::openPinDetailPage)
    }
    init {
        initializePersistentBottomSheet()
        initializeRecyclerView()
        bottomSheetBehavior.skipCollapsed = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun open(landmark: Landmark) {
        landmarkTitleTextView.text = landmark.placeName
        landmarkDescriptionTextView.text = landmark.content

        val placeholderDrawable = try {
            context.assets.open("pin/photo_placeholder.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
        } catch (e: IOException) { null }

        landmarkDetailImageView.load("${RetrofitClient.BASE_URL}/${landmark.detailImageUrl}") {
            crossfade(true)
            error(placeholderDrawable)
            placeholder(placeholderDrawable)
        }

        pinMemoAdapter.updateItems(
            PinRepository.getLandmarkPins(landmark)
        )
        pinMemoAdapter.sortBy(SortType.POPULAR)

        openBottomSheet()
    }
    private fun openBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun closeBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun toggleBottomSheet() {
        when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else -> Log.d("MainActivity", "Bottom sheet is in an unexpected state: ${bottomSheetBehavior.state}")
        }
    }

    private fun initializeRecyclerView() {
        landmarkRecyclerView.adapter = pinMemoAdapter
        landmarkRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun openPinDetailPage(pin: Pin) {
        val intent = Intent(context, PinDetailActivity::class.java).apply {
            putExtra("userId", pin.userId)
            putExtra("imageUrl", pin.imageUrl)
            putExtra("placeName", pin.placeName)
            putExtra("likeCount", pin.likeCount)
            putExtra("content", pin.content)
            putExtra("createdDate", pin.createdDate.toString())
        }
        context.startActivity(intent)
    }

    private fun initializePersistentBottomSheet() {

        bottomSheetBehavior = BottomSheetBehavior.from(landmarkBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                // BottomSheetBehavior state에 따른 이벤트
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d("MainActivity", "state: hidden")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("MainActivity", "state: expanded")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("MainActivity", "state: collapsed")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d("MainActivity", "state: dragging")
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        Log.d("MainActivity", "state: settling")
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        Log.d("MainActivity", "state: half expanded")
                    }
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })

    }
}