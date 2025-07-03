package com.dudoji.android.shop.activity

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.dudoji.android.R
import com.dudoji.android.shop.controller.ShopButtonsController

class ShopActivity: AppCompatActivity() {
    lateinit var shopButtonsController: ShopButtonsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        shopButtonsController = ShopButtonsController(findViewById(R.id.buttons_panel))

        setCloseButton()
    }

    fun setCloseButton() {
        findViewById<ImageButton>(R.id.close_button).setOnClickListener {
            finish()
        }
    }
}