package com.dudoji.android.presentation.map

import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.MapOverlayUiLayoutBinding
import com.dudoji.android.map.fragment.NpcListFragment
import com.dudoji.android.pin.util.PinSetterController
import com.dudoji.android.util.modal.Modal
import com.google.android.gms.maps.GoogleMap
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class MapOverlayUI(
    val binding: MapOverlayUiLayoutBinding,
    val activity: MapActivity,
    val assets: AssetManager,
    val googleMap: GoogleMap,
    val onPinDrop: (Double, Double) -> Unit) {

    var pinSetterController: PinSetterController? = null

    init {
        setPinSetterController()
        setupFilterBarToggle()

        binding.questModalButton.setOnClickListener {
            Modal.showCustomModal(
                activity,
                NpcListFragment(activity),
                R.layout.template_quest_modal
            )}

        val naviAssetPath = "file:///android_asset/navi/"
        binding.btnItem.load(naviAssetPath + "ic_item.png")

        binding.btnItem.setOnClickListener {
            binding.itemModal.visibility = if (binding.itemModal.isVisible) View.GONE else View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setPinSetterController() {
        try {
            val pinSetterBg = assets.open("pin/pin_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            binding.pinSetter.background = pinSetterBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pinSetterController = PinSetterController(
            binding.pinSetter,
            binding.outerDropZone,
            googleMap,
            onPinDrop
        )
    }

    private fun setupFilterBarToggle() {
        var isFilterBarVisible = false

        binding.btnFilter.setOnClickListener {
            isFilterBarVisible = !isFilterBarVisible
            if (isFilterBarVisible) {
                binding.filterBarWrapper.visibility = View.VISIBLE
                binding.filterBarAnim.progress = 0f
                binding.filterBarAnim.playAnimation()
            } else {
                binding.filterBarWrapper.visibility = View.GONE
            }
        }
    }
}