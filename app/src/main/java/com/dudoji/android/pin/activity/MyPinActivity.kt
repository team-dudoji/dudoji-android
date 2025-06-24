package com.dudoji.android.pin.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.databinding.ActivityMypinBinding
import com.dudoji.android.pin.repository.PinRepository
import com.dudoji.android.pin.util.PinMemoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MypinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypinBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadMyPins()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMyPins() {
        CoroutineScope(Dispatchers.IO).launch {
            val pins = PinRepository.getPins()
            withContext(Dispatchers.Main) {
                pins?.let { pins ->
                    val adapter = PinMemoAdapter(pins)
                    binding.recyclerView.adapter = adapter
                }
            }
        }
    }
}
