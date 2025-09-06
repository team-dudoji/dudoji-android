package com.dudoji.android.presentation.pin.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.databinding.PinSelectModalBinding
import com.dudoji.android.domain.repository.PinSkinRepository
import com.dudoji.android.pin.adapter.PinSkinAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class PinSkinChoiceDialogFragment: DialogFragment() {

    companion object {
        const val TAG = "PinSkinChoiceDialog"
        const val REQUEST_KEY = "PinSkinChoiceRequestKey"
        const val BUNDLE_KEY = "SelectedPinSkin"

        fun newInstance(): PinSkinChoiceDialogFragment {
            val fragment = PinSkinChoiceDialogFragment()
            return fragment
        }
    }

    private lateinit var binding: PinSelectModalBinding
    @Inject lateinit var pinSkinRepository: PinSkinRepository

    override fun onCreateDialog(savedInstanceState: Bundle?):Dialog {
        binding = PinSelectModalBinding.inflate(layoutInflater)
        binding.pinColorRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            binding.pinColorRecyclerView.adapter = PinSkinAdapter(
                pinSkinRepository.getPinSkins(),
                pinSkinRepository
            ) { selected ->
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to selected))
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("핀 스킨을 선택하세요")
            .setView(binding.root)
            .setNegativeButton("취소", null)
            .create()
    }
}
