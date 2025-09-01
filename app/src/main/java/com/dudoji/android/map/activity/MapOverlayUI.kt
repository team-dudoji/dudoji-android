package com.dudoji.android.map.activity

import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R
import com.dudoji.android.map.fragment.NpcListFragment
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.util.PinApplier
import com.dudoji.android.pin.util.PinSetterController
import com.dudoji.android.util.modal.Modal
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class MapOverlayUI(val assets: AssetManager, val activity: MapActivity, val googleMap: GoogleMap, val pinApplier: PinApplier, val clusterManager: ClusterManager<Pin>) {

    val questPageButton: ImageButton by lazy {
        activity.findViewById<ImageButton>(R.id.quest_modal_button)
    }
    val pinSetter: ImageView by lazy {
        activity.findViewById(R.id.pinSetter)
    }
    val pinDropZone: FrameLayout by lazy {
        activity.findViewById(R.id.outer_drop_zone)
    }
    val itemModal: LinearLayout by lazy {
        activity.findViewById(R.id.item_modal)
    }
    val itemButton = activity.findViewById<ImageButton>(R.id.btnItem)
    var pinSetterController: PinSetterController? = null

    init {
        setPinSetterController()
        setupFilterBarToggle()

        questPageButton.setOnClickListener {
            Modal.showCustomModal(
                activity,
                NpcListFragment(activity),
                R.layout.template_quest_modal
            )}

        val naviAssetPath = "file:///android_asset/navi/"
        itemButton.load(naviAssetPath + "ic_item.png")

        itemButton.setOnClickListener {
            itemModal.visibility = if (itemModal.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun setPinSetterController() {
        try {
            val pinSetterBg = assets.open("pin/pin_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            pinSetter.background = pinSetterBg
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pinSetterController = PinSetterController(pinSetter, pinDropZone ,pinApplier, googleMap, activity, clusterManager)
    }

    private fun setupFilterBarToggle() {
        val btnFilter = activity.findViewById<ImageButton>(R.id.btnFilter)
        val filterBarWrapper = activity.findViewById<FrameLayout>(R.id.filterBarWrapper)
        val filterBarAnim = activity.findViewById<LottieAnimationView>(R.id.filterBarAnim)

        var isFilterBarVisible = false

        btnFilter.setOnClickListener {
            isFilterBarVisible = !isFilterBarVisible
            if (isFilterBarVisible) {
                filterBarWrapper.visibility = View.VISIBLE
                filterBarAnim.progress = 0f
                filterBarAnim.playAnimation()
            } else {
                filterBarWrapper.visibility = View.GONE
            }
        }
    }
}