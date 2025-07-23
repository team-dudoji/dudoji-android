package com.dudoji.android.pin.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.databinding.ActivityMypinBinding
import com.dudoji.android.pin.adapter.PinMemoAdapter
import com.dudoji.android.pin.adapter.SortType
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypinBinding
    private var adapter: PinMemoAdapter = PinMemoAdapter(emptyList(), ::openPinDetailPage)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        goBack()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        setSpinner()
        loadMyPins()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMyPins() {
        CoroutineScope(Dispatchers.IO).launch {
            val pins = PinRepository.getPins()
            withContext(Dispatchers.Main) {
                pins.let { pins ->
                    adapter.updateItems(pins)
                    binding.recyclerView.adapter = adapter
                }
            }
        }
    }

    private fun openPinDetailPage(pin: Pin) {
        val intent = Intent(this, PinDetailActivity::class.java).apply {
            putExtra("userId", pin.userId)
            putExtra("imageUrl", pin.imageUrl)
            putExtra("placeName", pin.placeName)
            putExtra("likeCount", pin.likeCount)
            putExtra("content", pin.content)
            putExtra("createdDate", pin.createdDate.toString())
        }
        startActivity(intent)
    }

    private fun setSpinner() {
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val sortType = SortType.entries[position]
                adapter.sortBy(sortType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun goBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
