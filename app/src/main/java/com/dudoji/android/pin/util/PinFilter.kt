package com.dudoji.android.pin.util

import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who

class PinFilter(
    private val activity: AppCompatActivity,
) {

    // 각 who에 대한 가시성 맵
    private val visibilityMap = mutableMapOf(
        Who.MINE to true,
        Who.FOLLOWING to true,
        Who.UNKNOWN to true
    )

    //핀 필터 함수
    fun filterPins(pins: List<Pin>): List<Pin> {
        return pins.filter {
                pin -> visibilityMap[pin.master] == true
        }
    }

    //3종 필터 버튼 기능 함수, 토글로 껐다 켰다
    @RequiresApi(Build.VERSION_CODES.O)
    fun setupFilterButtons() {
        val btnMine = activity.findViewById<ImageButton>(R.id.btnFilterMine)
        val btnFriend = activity.findViewById<ImageButton>(R.id.btnFilterFriend)
        val btnStranger = activity.findViewById<ImageButton>(R.id.btnFilterStranger)

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

    //버튼 토글 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toggle(who: Who) {
        visibilityMap[who] = !(visibilityMap[who] ?: true)
    }


}
