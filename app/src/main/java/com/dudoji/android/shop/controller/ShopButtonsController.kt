package com.dudoji.android.shop.controller

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.dudoji.android.R

class ShopButtonsController(val view: View) {
    val buttons = listOf(
        ShopButton(view.findViewById<Button>(R.id.shop_dudoji_button)),
        ShopButton(view.findViewById<Button>(R.id.shop_pin_button)),
        ShopButton(view.findViewById<Button>(R.id.shop_item_button)),
        ShopButton(view.findViewById<Button>(R.id.shop_profile_button))
    )

    init {
        buttons.forEach { button ->
            button.button.setOnClickListener {
                onButtonClick(buttons.indexOf(button))
            }
        }
        onButtonClick(0)
    }

    fun onButtonClick(buttonId: Int) {
        buttons.forEach { it.setActive(false) }
        buttons[buttonId].setActive(true)
    }

    class ShopButton(val button: Button) {
        fun setActive(active: Boolean) {
            val params = button.layoutParams as LinearLayout.LayoutParams
            params.gravity = if (active) Gravity.TOP else Gravity.BOTTOM
            button.layoutParams = params
            ViewCompat.setBackgroundTintList(
                button,
                ColorStateList.valueOf(if (active) Color.parseColor("#82736C") else Color.parseColor("#FBFBFB"))
            )
            button.setTextColor(ColorStateList.valueOf(
                if (active) Color.parseColor("#FBFBFB") else Color.parseColor("#666560")
            ))
        }
    }
}