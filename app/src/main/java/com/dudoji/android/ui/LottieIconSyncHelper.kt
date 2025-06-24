package com.dudoji.android.ui

import android.animation.Animator
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView

object LottieIconSyncHelper {

    //아이콘이 lottie 애니메이션화가 안되서 대략 애니 동작 20%쯤 icon 동작하게 함
    fun setup(animView: LottieAnimationView, iconView: ImageView) {
        iconView.visibility = View.INVISIBLE

        animView.addAnimatorUpdateListener { animation ->
            val progress = animation.animatedFraction
            if (progress >= 0.2f && iconView.visibility != View.VISIBLE) {
                iconView.visibility = View.VISIBLE
            }
        }

        animView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                iconView.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                iconView.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
}