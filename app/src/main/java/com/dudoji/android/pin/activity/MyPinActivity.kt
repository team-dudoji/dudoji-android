package com.dudoji.android.pin.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.databinding.ActivityMypinBinding
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dudoji.android.pin.adapter.SortType
import com.dudoji.android.pin.adapter.PinMemoAdapter

class MyPinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypinBinding
    private var pinList: List<Pin> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        goBack()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadMyPins()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMyPins() {
        CoroutineScope(Dispatchers.IO).launch {
            val pins = PinRepository.getPins()
            withContext(Dispatchers.Main) {
                pins?.let { pins ->
                    pinList = pins
                    val adapter = PinMemoAdapter(pins)
                    binding.recyclerView.adapter = adapter

                    binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val sortType = SortType.values()[position]
                            adapter.sortBy(sortType)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }
            }
        }
    }

    private fun goBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
