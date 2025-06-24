package com.dudoji.android.pin.util

import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who

class PinFilter(
    private val activity: AppCompatActivity
) {
    private lateinit var btnMine: ImageButton
    private lateinit var btnFriend: ImageButton
    private lateinit var btnStranger: ImageButton

    // 각 who에 대한 가시성 맵
    private val visibilityMap = mutableMapOf(
        Who.MINE to true,
        Who.FOLLOWING to true,
        Who.UNKNOWN to true
    )

    // 핀 필터 함수
    fun filterPins(pins: List<Pin>): List<Pin> {
        return pins.filter { pin ->
            visibilityMap[pin.master] == true
        }
    }

    // 3종 필터 버튼 기능 함수, 토글로 껐다 켰다
    @RequiresApi(Build.VERSION_CODES.O)
    fun setupFilterButtons() {
        btnMine = activity.findViewById(R.id.btnFilterMine)
        btnFriend = activity.findViewById(R.id.btnFilterFriend)
        btnStranger = activity.findViewById(R.id.btnFilterStranger)

        btnMine.setOnClickListener {
            toggle(Who.MINE)
        }

        btnFriend.setOnClickListener {
            toggle(Who.FOLLOWING)
        }

        btnStranger.setOnClickListener {
            toggle(Who.UNKNOWN)
        }
    }

    // 버튼 토글 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toggle(who: Who) {
        val newState = !(visibilityMap[who] ?: true)
        visibilityMap[who] = newState

        val iconRes = if (newState) {
            when (who) {
                Who.MINE -> R.drawable.ic_mypin_enabled
                Who.FOLLOWING -> R.drawable.ic_friend_enabled
                Who.UNKNOWN -> R.drawable.ic_stranger_enabled
            }
        } else {
            when (who) {
                Who.MINE -> R.drawable.ic_mypin_disabled
                Who.FOLLOWING -> R.drawable.ic_friend_disabled
                Who.UNKNOWN -> R.drawable.ic_stranger_disabled
            }
        }

        when (who) {
            Who.MINE -> btnMine.setImageResource(iconRes)
            Who.FOLLOWING -> btnFriend.setImageResource(iconRes)
            Who.UNKNOWN -> btnStranger.setImageResource(iconRes)
        }
    }
}
