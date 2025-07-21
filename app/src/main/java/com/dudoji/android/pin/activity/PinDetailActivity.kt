package com.dudoji.android.pin.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dudoji.android.R
import com.dudoji.android.databinding.ActivityPinDetailBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PinDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPinDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadIntentData()
        lifecycleScope.launch {
            loadUserData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadUserData() {
        val userId = intent.getLongExtra("userId", -1L)

        if (userId == -1L) {
            return
        }

        val response = RetrofitClient.userApiService.getUserProfile(userId)

        if (response.isSuccessful.not()) {
            return
        }

        val user = response.body()
        user?.let {
            with(binding) {
                binding.btnFollow.visibility = View.GONE
                binding.textProfileName.text = it.name
                binding.imageProfile.load("${RetrofitClient.BASE_URL}/${it.profileImageUrl}") {
                    crossfade(true)
                    placeholder(R.drawable.user_placeholder)
                    error(R.drawable.user_placeholder)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadIntentData() {
        val imageUrl = intent.getStringExtra("imageUrl")
        val placeName = intent.getStringExtra("placeName") ?: ""
        val likeCount = intent.getIntExtra("likeCount", 0)
        val content = intent.getStringExtra("content") ?: ""
        val createdDateStr = intent.getStringExtra("createdDate")

        setupUI(imageUrl, placeName, likeCount, content, createdDateStr)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI(
        imageUrl: String?,
        placeName: String,
        likeCount: Int,
        content: String,
        createdDateStr: String?
    ) {
        with(binding) {
            imageView.load("${RetrofitClient.BASE_URL}/$imageUrl") {
                crossfade(true)
                placeholder(R.drawable.photo_placeholder)
                error(R.drawable.photo_placeholder)
            }

            textPlaceName.text = placeName
            textLikeCount.text = likeCount.toString()
            textContent.text = content
            textDate.text = formatDate(createdDateStr)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDate(dateStr: String?): String {
        return dateStr?.let {
            try {
                val date = LocalDate.parse(it)
                date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            } catch (e: Exception) {
                ""
            }
        } ?: ""
    }
}
