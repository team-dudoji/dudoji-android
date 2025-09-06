package com.dudoji.android.presentation.map

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.databinding.MapPinSelectBarBinding
import com.dudoji.android.domain.model.PinSkin
import com.dudoji.android.domain.repository.PinSkinRepository
import com.dudoji.android.pin.adapter.PinSkinAdapter

@RequiresApi(Build.VERSION_CODES.O)
class PinSelectBar(
    val binding: MapPinSelectBarBinding,
    val pinSkinRepository: PinSkinRepository,
    val onPinSkinSelected: (PinSkin) -> Unit
) {

    suspend fun init() {
        setupRecyclerView()
        val pinSkin = pinSkinRepository.getPinSkins()[0]
        onPinSkinSelected(pinSkin)
        setSelectedPinSkin(pinSkin)
    }

    suspend fun setSelectedPinSkin(pinSkin: PinSkin) {
        binding.pinSetter.background = pinSkinRepository.getPinSkinDrawableById(pinSkin.id, binding.root.context)
    }

    suspend fun setupRecyclerView() {
        binding.pinColorRecyclerView.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false)

        binding.pinColorRecyclerView.adapter = PinSkinAdapter(
            pinSkinRepository.getPinSkins(),
            pinSkinRepository,
            onPinSkinSelected
        )
    }
}