package com.dudoji.android.shop.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.dudoji.android.R
import com.dudoji.android.shop.controller.ShopButtonsController
import java.io.IOException

class ShopActivity : AppCompatActivity() {

    lateinit var shopButtonsController: ShopButtonsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        shopButtonsController = ShopButtonsController(findViewById(R.id.buttons_panel))

        setupViews()
    }

    private fun setupViews() {
        findViewById<ImageButton>(R.id.close_button).apply {
            load("file:///android_asset/shop/close.png")
            setOnClickListener { finish() }
        }

        findViewById<ImageButton>(R.id.coin_plus_button)
            .load("file:///android_asset/shop/coin_plus_button.png")

        findViewById<ImageView>(R.id.imageView2)
            .load("file:///android_asset/shop/shop_background.png")

        try {
            val coinBgDrawable = assets.open("shop/coin_background.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            findViewById<ConstraintLayout>(R.id.coin_container).background = coinBgDrawable

            val buttonBgDrawable = assets.open("shop/shop_select_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            findViewById<AppCompatButton>(R.id.shop_dudoji_button).background = buttonBgDrawable
            findViewById<AppCompatButton>(R.id.shop_pin_button).background = buttonBgDrawable?.constantState?.newDrawable()
            findViewById<AppCompatButton>(R.id.shop_item_button).background = buttonBgDrawable?.constantState?.newDrawable()
            findViewById<AppCompatButton>(R.id.shop_profile_button).background = buttonBgDrawable?.constantState?.newDrawable()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}