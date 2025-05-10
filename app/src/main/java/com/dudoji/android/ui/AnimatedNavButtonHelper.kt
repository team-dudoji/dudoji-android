package com.dudoji.android.ui

import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView

object AnimatedNavButtonHelper {

    fun setup(
        activity: Activity,
        centerButton: ImageView,
        leftButton: LottieAnimationView,
        rightButton: LottieAnimationView,
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit
    ) {
        centerButton.setOnClickListener {
            val shouldShow = leftButton.visibility != View.VISIBLE

            leftButton.visibility = if (shouldShow) View.VISIBLE else View.GONE
            rightButton.visibility = if (shouldShow) View.VISIBLE else View.GONE

            if (shouldShow) {
                leftButton.playAnimation()
                rightButton.playAnimation()
            } else {
                leftButton.cancelAnimation()
                rightButton.cancelAnimation()
            }
        }

        leftButton.setOnClickListener { onLeftClick() }
        rightButton.setOnClickListener { onRightClick() }
    }
}
