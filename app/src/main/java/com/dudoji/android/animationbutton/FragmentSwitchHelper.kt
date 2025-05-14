package com.dudoji.android.animationbutton

import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView

object FragmentSwitchHelper {

    fun setup(
        activity: AppCompatActivity,
        centerButton: ImageView,
        leftButton: LottieAnimationView,
        rightButton: LottieAnimationView,
        fragmentContainerId: Int,
        mapFragment: Fragment,
        mypageFragment: Fragment
    ) {
        var isExpanded = false

        centerButton.setOnClickListener {
            if (!isExpanded) {
                leftButton.visibility = View.VISIBLE
                rightButton.visibility = View.VISIBLE
                leftButton.playAnimation()
                rightButton.playAnimation()
            } else {
                leftButton.cancelAnimation()
                rightButton.cancelAnimation()
                leftButton.visibility = View.GONE
                rightButton.visibility = View.GONE
            }
            isExpanded = !isExpanded
        }

        leftButton.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(fragmentContainerId, mapFragment)
                .commit()
            leftButton.visibility = View.GONE
            rightButton.visibility = View.GONE
            isExpanded = false
        }

        rightButton.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(fragmentContainerId, mypageFragment)
                .commit()
            leftButton.visibility = View.GONE
            rightButton.visibility = View.GONE
            isExpanded = false
        }
    }
}
