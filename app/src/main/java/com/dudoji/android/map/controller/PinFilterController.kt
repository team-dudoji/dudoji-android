package com.dudoji.android.map.controller

import android.os.Build
import android.util.Log
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.map.domain.pin.Pin
import com.dudoji.android.map.domain.pin.Who
import com.dudoji.android.map.repository.PinRepository
import com.dudoji.android.map.utils.pin.PinApplier

class PinFilterController(
    private val activity: AppCompatActivity,
    private val pinApplier: PinApplier
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
        Log.d("PinFilter", "🔁 ${who.name} toggled: ${visibilityMap[who]}")
        applyFilteredPins()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun applyFilteredPins() {
        val allPins = PinRepository.getPins() // 핀정보 가져옴

        allPins.forEach { pin ->
            Log.d("PinFilter", "Pin: ${pin.title}, who: ${pin.master}, visible: ${visibilityMap[pin.master]}")
        }

        // 전체 핀 리스트에서 값이 true인 핀들만 걸러낸다.
        val filteredPins = filterPins(allPins)

        //핀 개수 초기화
        val counts = mutableMapOf(
            Who.MINE to 0,
            Who.FOLLOWING to 0,
            Who.UNKNOWN to 0
        )

        //순환하며 값 세기
        filteredPins.forEach {
            counts[it.master] = counts.getOrDefault(it.master, 0) + 1
        }

        Log.d("PinFilter", "필터 적용 중인 핀 개수:")
        Log.d("PinFilter", "  내 핀: ${counts[Who.MINE]}")
        Log.d("PinFilter", "  친구 핀: ${counts[Who.FOLLOWING]}")
        Log.d("PinFilter", "  모르는 사람 핀: ${counts[Who.UNKNOWN]}")
        
        pinApplier.clearPins()
        pinApplier.applyPins(filteredPins)
    }
}
