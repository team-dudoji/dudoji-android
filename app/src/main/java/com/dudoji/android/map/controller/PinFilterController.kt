package com.dudoji.android.map.controller

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.follow.repository.FollowRepository
import com.dudoji.android.map.domain.Pin
import com.dudoji.android.map.repository.PinRepository
import com.dudoji.android.map.utils.pin.PinApplier

class PinFilterController(
    private val activity: AppCompatActivity,
    private val pinApplier: PinApplier,
    private val currentUserId: Long,
    private val friendIdSet: Set<Long>
) {
    private var isMineVisible = true
    private var isFriendVisible = true
    private var isStrangerVisible = true


    @RequiresApi(Build.VERSION_CODES.O)
    fun setupFilterButtons() {
        val btnMine = activity.findViewById<ImageButton>(R.id.btnFilterMine)
        val btnFriend = activity.findViewById<ImageButton>(R.id.btnFilterFriend)
        val btnStranger = activity.findViewById<ImageButton>(R.id.btnFilterStranger)

        btnMine.setOnClickListener {
            isMineVisible = !isMineVisible
            applyFilteredPins()
        }

        btnFriend.setOnClickListener {
            isFriendVisible = !isFriendVisible
            applyFilteredPins()
        }

        btnStranger.setOnClickListener {
            isStrangerVisible = !isStrangerVisible
            applyFilteredPins()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyFilteredPins() {
        val allPins = PinRepository.getPins()
        val filteredPins = allPins.filter { pin ->
            when (pin.userId) {
                currentUserId -> isMineVisible
                in friendIdSet -> isFriendVisible
                else -> isStrangerVisible
            }
        }

        Log.d("PinFilter", "📌 총 ${filteredPins.size}개 핀 적용됨:")
        filteredPins.forEach {
            Log.d("PinFilter", "  - ${it.title} (${it.userId})")
        }

        pinApplier.clearPins()
        pinApplier.applyPins(filteredPins)
    }
}
