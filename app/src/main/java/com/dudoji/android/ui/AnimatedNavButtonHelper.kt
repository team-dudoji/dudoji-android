package com.dudoji.android.ui

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity

object AnimatedNavButtonHelper {

    private var isExpanded = false

    fun setup(
        activity: MapActivity,
        onCenterClick: (() -> Unit)? = null,
        onStoreClick: () -> Unit,
        onMyPinClick: () -> Unit,
        onSocialClick: () -> Unit,
        onProfileClick: () -> Unit
    ) {
        val centerButton = activity.findViewById<ImageView>(R.id.centerButton)

        val filterBarAnim = activity.findViewById<LottieAnimationView>(R.id.filterBarAnim)

        val storeWrapper = activity.findViewById<FrameLayout>(R.id.storeButtonWrapper)
        val storeAnim = activity.findViewById<LottieAnimationView>(R.id.storeButtonAnim)
        val storeIcon = activity.findViewById<ImageView>(R.id.storeIcon)

        val profileWrapper = activity.findViewById<FrameLayout>(R.id.profileButtonWrapper)
        val profileAnim = activity.findViewById<LottieAnimationView>(R.id.profileButtonAnim)
        val profileIcon = activity.findViewById<ImageView>(R.id.profileIcon)

        val mypinWrapper = activity.findViewById<FrameLayout>(R.id.myPinButtonWrapper)
        val mypinAnim = activity.findViewById<LottieAnimationView>(R.id.myPinButtonAnim)
        val mypinIcon = activity.findViewById<ImageView>(R.id.myPinIcon)

        val socialWrapper = activity.findViewById<FrameLayout>(R.id.socialButtonWrapper)
        val socialAnim = activity.findViewById<LottieAnimationView>(R.id.socialButtonAnim)
        val socialIcon = activity.findViewById<ImageView>(R.id.socialIcon)

        val btnFriend = activity.findViewById<ImageButton>(R.id.btnFilterFriend)
        val btnMine = activity.findViewById<ImageButton>(R.id.btnFilterMine)
        val btnStranger = activity.findViewById<ImageButton>(R.id.btnFilterStranger)
        val btnFilter = activity.findViewById<ImageButton>(R.id.btnFilter)
        val pinSetter = activity.findViewById<ImageView>(R.id.pinSetter)
        val questPageButton = activity.findViewById<ImageButton>(R.id.quest_modal_button)

        val btnItem = activity.findViewById<ImageButton>(R.id.btnItem)
        val searchBar = activity.findViewById<View>(R.id.search_bar_container)
        val myLocationBtn = activity.findViewById<View>(R.id.my_location_button)

        val naviAssetPath = "file:///android_asset/navi/"
        btnStranger.load(naviAssetPath + "ic_stranger_enabled.png")
        btnFriend.load(naviAssetPath + "ic_friend_enabled.png")
        btnMine.load(naviAssetPath + "ic_mypin_enabled.png")
        btnFilter.load(naviAssetPath + "ic_filter.png")
        storeIcon.load(naviAssetPath + "ic_store.png")
        profileIcon.load(naviAssetPath + "ic_profile.png")
        mypinIcon.load(naviAssetPath + "ic_mypin.png")
        socialIcon.load(naviAssetPath + "ic_social.png")
        centerButton.load(naviAssetPath + "ic_center_button_open.png")
        btnItem.load(naviAssetPath + "ic_item.png")

        LottieIconSyncHelper.setup(storeAnim, storeIcon)
        LottieIconSyncHelper.setup(profileAnim, profileIcon)
        LottieIconSyncHelper.setup(mypinAnim, mypinIcon)
        LottieIconSyncHelper.setup(socialAnim, socialIcon)

        fun setExpanded(expanded: Boolean) {
            isExpanded = expanded
            val vis = if (expanded) View.VISIBLE else View.GONE

            if (expanded) {
                storeAnim.playAnimation()
                profileAnim.playAnimation()
                mypinAnim.playAnimation()
                socialAnim.playAnimation()
                centerButton.load(naviAssetPath + "ic_center_button_close.png")
            } else {
                storeAnim.cancelAnimation()
                profileAnim.cancelAnimation()
                mypinAnim.cancelAnimation()
                socialAnim.cancelAnimation()
                centerButton.load(naviAssetPath + "ic_center_button_open.png")
            }

            storeWrapper.visibility = vis
            profileWrapper.visibility = vis
            mypinWrapper.visibility = vis
            socialWrapper.visibility = vis
            questPageButton.visibility = vis

            pinSetter.visibility = vis
            btnFilter.visibility = vis
            btnItem.visibility = vis
            searchBar.visibility = vis
            myLocationBtn.visibility = vis

            btnFriend.visibility = vis
            btnMine.visibility = vis
            btnStranger.visibility = vis
            filterBarAnim.visibility = vis
        }

        setExpanded(false)

        centerButton.setOnClickListener {
            setExpanded(!isExpanded)
            onCenterClick?.invoke()
        }

        storeWrapper.setOnClickListener { onStoreClick() }
        profileWrapper.setOnClickListener { onProfileClick() }
        mypinWrapper.setOnClickListener { onMyPinClick() }
        socialWrapper.setOnClickListener { onSocialClick() }
    }
}
