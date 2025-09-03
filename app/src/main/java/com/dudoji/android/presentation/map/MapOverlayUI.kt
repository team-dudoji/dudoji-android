package com.dudoji.android.presentation.map

import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityMapBinding
import com.dudoji.android.map.fragment.NpcListFragment
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.util.PinApplier
import com.dudoji.android.pin.util.PinSetterController
import com.dudoji.android.util.modal.Modal
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class MapOverlayUI(val binding: ActivityMapBinding, val activity: MapActivity, val assets: AssetManager, val googleMap: GoogleMap, val pinApplier: PinApplier, val clusterManager: ClusterManager<Pin>) {

    var pinSetterController: PinSetterController? = null

    init {
        setPinSetterController()
        setupFilterBarToggle()

        binding.mapOverlayUiLayout.questModalButton.setOnClickListener {
            Modal.showCustomModal(
                activity,
                NpcListFragment(activity),
                R.layout.template_quest_modal
            )}

        val naviAssetPath = "file:///android_asset/navi/"
        binding.mapOverlayUiLayout.btnItem.load(naviAssetPath + "ic_item.png")

        binding.mapOverlayUiLayout.btnItem.setOnClickListener {
            binding.mapOverlayUiLayout.itemModal.visibility = if (binding.mapOverlayUiLayout.itemModal.isVisible) View.GONE else View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setPinSetterController() {
        try {
            val pinSetterBg = assets.open("pin/pin_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            binding.mapOverlayUiLayout.pinSetter.background = pinSetterBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pinSetterController = PinSetterController(
            binding.mapOverlayUiLayout.pinSetter,
            binding.outerDropZone,
            pinApplier,
            googleMap,
            activity,
            clusterManager
        )
    }

    private fun setupFilterBarToggle() {
        var isFilterBarVisible = false

        binding.mapOverlayUiLayout.btnFilter.setOnClickListener {
            isFilterBarVisible = !isFilterBarVisible
            if (isFilterBarVisible) {
                binding.mapOverlayUiLayout.filterBarWrapper.visibility = View.VISIBLE
                binding.mapOverlayUiLayout.filterBarAnim.progress = 0f
                binding.mapOverlayUiLayout.filterBarAnim.playAnimation()
            } else {
                binding.mapOverlayUiLayout.filterBarWrapper.visibility = View.GONE
            }
        }
    }
}