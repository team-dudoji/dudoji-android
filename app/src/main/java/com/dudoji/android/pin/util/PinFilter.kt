package com.dudoji.android.pin.util

import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.map.manager.DatabaseMapSectionManager
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.domain.Who

class PinFilter(
    private val activity: AppCompatActivity,
    private val databaseMapSectionManager: DatabaseMapSectionManager?
) {
    private lateinit var btnMine: ImageButton
    private lateinit var btnFriend: ImageButton
    private lateinit var btnStranger: ImageButton

    private val visibilityMap = mutableMapOf(
        Who.MINE to true,
        Who.FOLLOWING to true,
        Who.UNKNOWN to true
    )

    fun filterPins(pins: List<Pin>): List<Pin> {
        return pins.filter { pin ->
            visibilityMap[pin.master] == true
                    && !(databaseMapSectionManager?.isFogExists(pin.lat, pin.lng) ?: false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupFilterButtons() {
        btnMine = activity.findViewById(R.id.btnFilterMine)
        btnFriend = activity.findViewById(R.id.btnFilterFriend)
        btnStranger = activity.findViewById(R.id.btnFilterStranger)
        val btnFilter = activity.findViewById<ImageButton>(R.id.btnFilter)


        val naviAssetPath = "file:///android_asset/navi/"
        btnStranger.load(naviAssetPath + "ic_stranger_enabled.png")
        btnFriend.load(naviAssetPath + "ic_friend_enabled.png")
        btnMine.load(naviAssetPath + "ic_mypin_enabled.png")
        btnFilter.load(naviAssetPath + "ic_filter.png")

        btnMine.setOnClickListener {
            toggle(Who.MINE)
            (activity as MapActivity).mapOverlayUI?.pinSetterController?.pinApplier?.markForReload()
        }

        btnFriend.setOnClickListener {
            toggle(Who.FOLLOWING)
            (activity as MapActivity).mapOverlayUI?.pinSetterController?.pinApplier?.markForReload()
        }

        btnStranger.setOnClickListener {
            toggle(Who.UNKNOWN)
            (activity as MapActivity).mapOverlayUI?.pinSetterController?.pinApplier?.markForReload()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toggle(who: Who) {
        val newState = !(visibilityMap[who] ?: true)
        visibilityMap[who] = newState

        val iconFileName = if (newState) {
            when (who) {
                Who.MINE -> "ic_mypin_enabled.png"
                Who.FOLLOWING -> "ic_friend_enabled.png"
                Who.UNKNOWN -> "ic_stranger_enabled.png"
            }
        } else {
            when (who) {
                Who.MINE -> "ic_mypin_disabled.png"
                Who.FOLLOWING -> "ic_friend_disabled.png"
                Who.UNKNOWN -> "ic_stranger_disabled.png"
            }
        }

        val assetPath = "file:///android_asset/navi/$iconFileName"

        when (who) {
            Who.MINE -> btnMine.load(assetPath)
            Who.FOLLOWING -> btnFriend.load(assetPath)
            Who.UNKNOWN -> btnStranger.load(assetPath)
        }
    }
}