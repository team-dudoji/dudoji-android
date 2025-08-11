package com.dudoji.android.util.modal

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R

object Modal {
    fun showCustomModal(activity: Activity,
                        contentLayoutRes: Int,
                        templateLayoutRes: Int = R.layout.template_modal,
                        onBind: (View) -> Unit) {
        val inflater = LayoutInflater.from(activity)
        val modalRoot = inflater.inflate(templateLayoutRes, null) as ViewGroup
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

    fun showCustomModal(
        activity: AppCompatActivity,
        fragment: ModalFragment,
        templateLayoutRes: Int = R.layout.template_modal
    ) {
        val inflater = LayoutInflater.from(activity)
        val modalRoot = inflater.inflate(templateLayoutRes, null) as ViewGroup

        val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(modalRoot)

        fragment.setCloseFun{
            activity.runOnUiThread {
                (modalRoot.parent as? ViewGroup)?.removeView(modalRoot)
                activity.supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
        }

        modalRoot.setOnClickListener {
            fragment.close()
        }

        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.modal_content, fragment, "modal_fragment")
            .commit()
    }
}