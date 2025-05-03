package com.dudoji.android.util.modal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R

object Modal {
    fun showCustomModal(activity: AppCompatActivity,
                        contentLayoutRes: Int,
                        onBind: (View) -> Unit) {
        val inflater = LayoutInflater.from(activity)
        val modalRoot = inflater.inflate(R.layout.template_modal, null) as ViewGroup
        val contentContainer = modalRoot.findViewById<FrameLayout>(R.id.modal_content)

        val contentView = inflater.inflate(contentLayoutRes, contentContainer, false)
        contentContainer.addView(contentView)

        onBind(contentView)

        modalRoot.setOnClickListener {
            (modalRoot.parent as? ViewGroup)?.removeView(modalRoot)
        }

        contentContainer.setOnClickListener { /* swallow */ }

        val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(modalRoot)
    }
}