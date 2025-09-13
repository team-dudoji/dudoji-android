package com.dudoji.android.presentation.map

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.dudoji.android.R
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileButtonManager(
    private val context: Context,
    private val profileButton: ShapeableImageView,
    private val profileSelectorBar: LinearLayout,
    private val lottieView: LottieAnimationView,
    private val option1: ShapeableImageView,
    private val option2: ShapeableImageView,
    private val option3: ShapeableImageView,
    private val option4: ShapeableImageView,
    private val viewModel: MapViewModel,
    private val scope: CoroutineScope
) {
    private var isBarExpanded = false
    private var isAnimationRunning = false

    fun init() {
        profileButton.setOnClickListener {
            toggleProfileBar()
        }
        setupOptionListeners()
        observeSelectedProfile()
        setupLottieListener()
    }

    private fun setupOptionListeners() {
        val options = mapOf(
            option1 to R.drawable.profile_image1,
            option2 to R.drawable.profile_image2,
            option3 to R.drawable.profile_image3,
            option4 to R.drawable.profile_image4
        )

        options.forEach { (imageView, resId) ->
            imageView.setOnClickListener {
                viewModel.setSelectedProfileImage(resId)
                toggleProfileBar(expand = false)
            }
        }
    }

    private fun observeSelectedProfile() {
        scope.launch {
            viewModel.selectedProfileImageResId.collectLatest { resId ->
                profileButton.setImageResource(resId)
            }
        }
    }

    private fun toggleProfileBar(expand: Boolean? = null) {
        if (isAnimationRunning) return

        isBarExpanded = expand ?: !isBarExpanded
        isAnimationRunning = true

        if (isBarExpanded) {
            lottieView.visibility = View.VISIBLE
            lottieView.speed = 1f
            lottieView.playAnimation()
            profileSelectorBar.visibility = View.VISIBLE
        } else {
            lottieView.speed = -1f
            lottieView.playAnimation()
            profileSelectorBar.visibility = View.GONE
        }
    }

    private fun setupLottieListener() {
        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                if (!isBarExpanded) {
                    lottieView.visibility = View.GONE
                }
                isAnimationRunning = false
            }
        })
    }

    fun updateVisibility(isNavExpanded: Boolean) {
        val visibility = if (isNavExpanded) View.GONE else View.VISIBLE
        profileButton.visibility = visibility

        if (isNavExpanded) {
            lottieView.visibility = View.GONE
            profileSelectorBar.visibility = View.GONE
            isBarExpanded = false
        }
    }
}