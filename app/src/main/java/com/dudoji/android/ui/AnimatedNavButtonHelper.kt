package com.dudoji.android.ui

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
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

        val pinSetter = activity.findViewById<ImageView>(R.id.pinSetter)
        val btnFilter = activity.findViewById<ImageButton>(R.id.btnFilter)

        LottieIconSyncHelper.setup(storeAnim, storeIcon)
        LottieIconSyncHelper.setup(profileAnim, profileIcon)
        LottieIconSyncHelper.setup(mypinAnim, mypinIcon)
        LottieIconSyncHelper.setup(socialAnim, socialIcon)
        centerButton.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                storeWrapper.visibility = View.VISIBLE
                profileWrapper.visibility = View.VISIBLE
                mypinWrapper.visibility = View.VISIBLE
                socialWrapper.visibility = View.VISIBLE

                storeAnim.playAnimation()
                profileAnim.playAnimation()
                mypinAnim.playAnimation()
                socialAnim.playAnimation()

                pinSetter.visibility = View.VISIBLE
                btnFilter.visibility = View.VISIBLE

                centerButton.setImageResource(R.drawable.ic_center_button_close)

                btnFriend.visibility = View.VISIBLE
                btnMine.visibility = View.VISIBLE
                btnStranger.visibility = View.VISIBLE
                filterBarAnim.visibility = View.VISIBLE
            } else {
                storeAnim.cancelAnimation()
                profileAnim.cancelAnimation()
                mypinAnim.cancelAnimation()
                socialAnim.cancelAnimation()

                storeWrapper.visibility = View.GONE
                profileWrapper.visibility = View.GONE
                mypinWrapper.visibility = View.GONE
                socialWrapper.visibility = View.GONE

                pinSetter.visibility = View.GONE
                btnFilter.visibility = View.GONE

                centerButton.setImageResource(R.drawable.ic_center_button_open)

                // 필터 바 닫기
                btnFriend.visibility = View.GONE
                btnMine.visibility = View.GONE
                btnStranger.visibility = View.GONE

                filterBarAnim.visibility = View.GONE
            }

            onCenterClick?.invoke()
        }

        storeWrapper.setOnClickListener { onStoreClick() }
        profileWrapper.setOnClickListener { onProfileClick() }
        mypinWrapper.setOnClickListener { onMyPinClick() }
        socialWrapper.setOnClickListener { onSocialClick() }
    }
}
