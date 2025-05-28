package com.dudoji.android.ui

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R

object AnimatedNavButtonHelper {

    private var isExpanded = false

    fun setup(
        centerButton: ImageView,
        storeWrapper: FrameLayout,
        storeButton: LottieAnimationView,
        profileWrapper: FrameLayout,
        profileButton: LottieAnimationView,
        socialWrapper: FrameLayout,
        socialButton: LottieAnimationView,
        mypinWrapper: FrameLayout,
        mypinButton: LottieAnimationView,
        btnFriend: ImageButton,
        btnMine: ImageButton,
        btnStranger: ImageButton,
        pinSetter: ImageView,
        btnFilter: ImageButton,
        onCenterClick: (() -> Unit)? = null,
        onStoreClick: () -> Unit,
        onMyPinClick: () -> Unit,
        onSocialClick: () -> Unit,
        onProfileClick: () -> Unit
    ) {
        centerButton.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                storeWrapper.visibility = View.VISIBLE
                profileWrapper.visibility = View.VISIBLE
                mypinWrapper.visibility = View.VISIBLE
                socialWrapper.visibility = View.VISIBLE

                storeButton.playAnimation()
                profileButton.playAnimation()
                mypinButton.playAnimation()
                socialButton.playAnimation()

                pinSetter.visibility = View.VISIBLE
                btnFilter.visibility = View.VISIBLE

                centerButton.setImageResource(R.drawable.ic_center_button_close)

                btnFriend.visibility = View.VISIBLE
                btnMine.visibility = View.VISIBLE
                btnStranger.visibility = View.VISIBLE
            } else {
                storeButton.cancelAnimation()
                profileButton.cancelAnimation()
                mypinButton.cancelAnimation()
                socialButton.cancelAnimation()

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
            }

            onCenterClick?.invoke()
        }

        storeWrapper.setOnClickListener { onStoreClick() }
        profileWrapper.setOnClickListener { onProfileClick() }
        mypinWrapper.setOnClickListener { onMyPinClick() }
        socialWrapper.setOnClickListener { onSocialClick() }
    }
}
