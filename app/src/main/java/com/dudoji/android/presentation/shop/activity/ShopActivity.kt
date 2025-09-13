package com.dudoji.android.presentation.shop.activity

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.dudoji.android.R
import com.dudoji.android.mypage.repository.MyPageRemoteDataSource
import com.dudoji.android.presentation.shop.controller.ShopButtonsController
import java.io.IOException

class ShopActivity : AppCompatActivity() {

    lateinit var shopButtonsController: ShopButtonsController

    private val closeButton by lazy {
        findViewById<ImageButton>(R.id.close_button)
    }
    private val coinPlusButton by lazy {
        findViewById<ImageButton>(R.id.coin_plus_button)
    }
    private val shopBackground by lazy {
        findViewById<ImageView>(R.id.imageView2)
    }
    private val coinContainer by lazy {
        findViewById<ConstraintLayout>(R.id.coin_container)
    }
    private val shopDudojiButton by lazy {
        findViewById<AppCompatButton>(R.id.shop_dudoji_button)
    }
    private val shopPinButton by lazy {
        findViewById<AppCompatButton>(R.id.shop_pin_button)
    }
    private val shopItemButton by lazy {
        findViewById<AppCompatButton>(R.id.shop_item_button)
    }
    private val shopProfileButton by lazy {
        findViewById<AppCompatButton>(R.id.shop_profile_button)
    }
    private val coinTextView by lazy {
        findViewById<TextView>(R.id.coin_text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        shopButtonsController = ShopButtonsController(findViewById(R.id.buttons_panel))

        setupViews()
        loadCoin()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadCoin() {
        coinTextView.text = MyPageRemoteDataSource.userProfile?.coin?.toString() ?: "0"
        MyPageRemoteDataSource.loadCoin { coin ->
            Log.d("ShopActivity", "Coin loaded: $coin")
            coinTextView.text = coin.toString()
        }
    }

    private fun setupViews() {
        closeButton.apply {
            load("file:///android_asset/shop/close.png")
            setOnClickListener { finish() }
        }
        coinPlusButton
            .load("file:///android_asset/shop/coin_plus_button.png")
        shopBackground
            .load("file:///android_asset/shop/shop_background.png")

        try {
            val coinBgDrawable = assets.open("shop/coin_background.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            coinContainer.background = coinBgDrawable

            val buttonBgDrawable = assets.open("shop/shop_select_button.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            shopDudojiButton.background = buttonBgDrawable
            shopPinButton.background = buttonBgDrawable?.constantState?.newDrawable()
            shopItemButton.background = buttonBgDrawable?.constantState?.newDrawable()
            shopProfileButton.background = buttonBgDrawable?.constantState?.newDrawable()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}