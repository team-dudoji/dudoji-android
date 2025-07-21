package com.dudoji.android.pin.fragment

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.databinding.EditPinMemoModalBinding
import com.dudoji.android.pin.domain.PinSkin
import com.dudoji.android.pin.repository.PinSkinRepository
import com.dudoji.android.pin.util.PinMakeData
import com.dudoji.android.util.WeekTranslator
import com.dudoji.android.util.modal.ModalFragment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

class PinMemoInputFragment(
    private val lat: Double,
    private val lng: Double,
    private val activity: AppCompatActivity,
    private val onComplete: (PinMakeData) -> Unit
) : ModalFragment() {

    private var _binding: EditPinMemoModalBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private var address: String = "주소를 가져오는 중..."
    private var selectedPinColor: PinSkin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.pinMemoImage.setImageURI(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EditPinMemoModalBinding.inflate(inflater, container, false)
        initViews()
        setupListeners()
        binding.root.setOnTouchListener { _, _ ->
            true
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initViews() {
        loadAddress()
        updateDateText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        binding.pinMemoImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.memoDateEditButton.setOnClickListener {
            showDatePicker()
        }

        binding.pinColorSelectButton.setOnClickListener {
            val dialog = PinSkinChoiceDialogFragment(
                PinSkinRepository.pinSkinList?.values?.toList() ?: emptyList()
            ) { selectedSkin ->
                selectedPinColor = selectedSkin
                lifecycleScope.launch {
                    binding.pinColorSelectButton.background =
                        PinSkinRepository.getPinSkinDrawableById(selectedSkin.id, requireContext())
                }
                Toast.makeText(requireContext(), "${selectedSkin.name} 핀이 선택되었습니다.", Toast.LENGTH_SHORT).show()
            }
            dialog.show(parentFragmentManager, "PinSkinDialog")
        }

        binding.memoSaveButton.setOnClickListener {
            saveMemo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loadAddress() {
        Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lng, 1) { addresses ->
            address = addresses.firstOrNull()?.getAddressLine(0) ?: "주소를 가져올 수 없습니다."
            binding.pinAddress.text = address
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                updateDateText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7 // 7 days ago
        }.show()
    }

    private fun updateDateText() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val weekDay = WeekTranslator.translateWeekToKorean(calendar.get(Calendar.DAY_OF_WEEK))
        binding.memoDate.text = "$year.$month.$day ($weekDay)"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMemo() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val date = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val data = PinMakeData(
            placeName = binding.pinPlaceName.text.toString(),
            content = binding.memoContentInput.text.toString(),
            date = date,
            imageUri = selectedImageUri!!,
            address = address,
            pinSkinId = selectedPinColor?.id ?: 1L
        )

        onComplete(data)
        close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
