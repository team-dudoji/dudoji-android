package com.dudoji.android.pin.util

import android.os.Build
import androidx.annotation.RequiresApi
import coil.load
import com.dudoji.android.databinding.MapOverlayUiLayoutBinding
import com.dudoji.android.pin.domain.Who
import com.dudoji.android.presentation.map.MapViewModel

@RequiresApi(Build.VERSION_CODES.O)
class PinFilter(
    private val binding: MapOverlayUiLayoutBinding,
    private val mapViewModel: MapViewModel
) {

    init {
        setupFilterButtons()
    }

    fun setupFilterButtons() {
        val naviAssetPath = "file:///android_asset/navi/"
        binding.btnFilterStranger.load(naviAssetPath + "ic_stranger_enabled.png")
        binding.btnFilterFriend.load(naviAssetPath + "ic_friend_enabled.png")
        binding.btnFilterMine.load(naviAssetPath + "ic_mypin_enabled.png")
        binding.btnFilter.load(naviAssetPath + "ic_filter.png")

        binding.btnFilterMine.setOnClickListener {
            mapViewModel.toggleVisibility(Who.MINE)
        }

        binding.btnFilterFriend.setOnClickListener {
            mapViewModel.toggleVisibility(Who.FOLLOWING)
        }

        binding.btnFilterStranger.setOnClickListener {
            mapViewModel.toggleVisibility(Who.UNKNOWN)
        }
    }

    fun updateFilterButton(who: Who, isVisible: Boolean) {
        val iconFileName = if (isVisible) {
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
            Who.MINE -> binding.btnFilterMine.load(assetPath)
            Who.FOLLOWING -> binding.btnFilterFriend.load(assetPath)
            Who.UNKNOWN -> binding.btnFilterStranger.load(assetPath)
        }
    }
}