package com.dudoji.android.pin.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityPinDetailBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PinDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPinDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("imageUrl")
        val placeName = intent.getStringExtra("placeName")
        val likeCount = intent.getIntExtra("likeCount", 0)
        val content = intent.getStringExtra("content")
        val createdDateStr = intent.getStringExtra("createdDate")

        binding.imageView.load("${RetrofitClient.BASE_URL}/$imageUrl") {
            crossfade(true)
            error(R.drawable.photo_placeholder)
            placeholder(R.drawable.photo_placeholder)
        }

        binding.textPlaceName.text = placeName
        binding.textLikeCount.text = likeCount.toString()
        binding.textContent.text = content

        val formattedDate = createdDateStr?.let {
            val date = LocalDate.parse(it)
            date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        } ?: ""
        binding.textDate.text = formattedDate
    }
}
