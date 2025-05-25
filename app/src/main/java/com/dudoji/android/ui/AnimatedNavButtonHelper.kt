package com.dudoji.android.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R

object AnimatedNavButtonHelper {

    private var isExpanded = false

    fun setup(
        activity: Activity,
        centerButton: ImageView,
        storeButton: LottieAnimationView,
        myPinButton: LottieAnimationView,
        socialButton: LottieAnimationView,
        profileButton: LottieAnimationView,
        onCenterClick: (() -> Unit)? = null,
        onStoreClick: () -> Unit,
        onMyPinClick: () -> Unit,
        onSocialClick: () -> Unit,
        onProfileClick: () -> Unit
    ) {
        centerButton.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                storeButton.visibility = View.VISIBLE
                myPinButton.visibility = View.VISIBLE
                socialButton.visibility = View.VISIBLE
                profileButton.visibility = View.VISIBLE

                storeButton.playAnimation()
                myPinButton.playAnimation()
                socialButton.playAnimation()
                profileButton.playAnimation()

                // 중앙 버튼 아이콘 변경
                centerButton.setImageResource(R.drawable.ic_center_button_close)

            } else {
                storeButton.cancelAnimation()
                myPinButton.cancelAnimation()
                socialButton.cancelAnimation()
                profileButton.cancelAnimation()

                storeButton.visibility = View.GONE
                myPinButton.visibility = View.GONE
                socialButton.visibility = View.GONE
                profileButton.visibility = View.GONE

                // 다시 원래 아이콘으로
                centerButton.setImageResource(R.drawable.ic_center_button_open)
            }

            onCenterClick?.invoke()
        }

        storeButton.setOnClickListener { onStoreClick() }
        myPinButton.setOnClickListener { onMyPinClick() }
        socialButton.setOnClickListener { onSocialClick() }
        profileButton.setOnClickListener { onProfileClick() }
    }

    fun setupProfileOnly(
        activity: Activity,
        centerButton: ImageView,
        profileWrapper: FrameLayout,
        profileAnim: LottieAnimationView,
        onProfileClick: () -> Unit
    ) {
        var isExpanded = false

        centerButton.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                profileWrapper.visibility = View.VISIBLE
                profileAnim.progress = 0f
                profileAnim.playAnimation()
                centerButton.setImageResource(R.drawable.ic_center_button_close)
            } else {
                profileAnim.cancelAnimation()
                profileWrapper.visibility = View.GONE
                centerButton.setImageResource(R.drawable.ic_center_button_open)
            }
        }

        profileWrapper.setOnClickListener {
            onProfileClick()
        }
    }


}

