package com.dudoji.android.presentation.pin.fragment

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.databinding.EditPinMemoModalBinding
import com.dudoji.android.domain.model.PinSkin
import com.dudoji.android.domain.repository.PinSkinRepository
import com.dudoji.android.landmark.adapter.EditableHashtagAdapter
import com.dudoji.android.pin.util.PinMakeData
import com.dudoji.android.util.WeekTranslator
import com.dudoji.android.util.modal.ModalFragment
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.util.Locale

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class PinMemoInputFragment(
    private val lat: Double,
    private val lng: Double,
    private val onComplete: (PinMakeData) -> Unit
) : ModalFragment() {

    companion object {

    }

    private var _binding: EditPinMemoModalBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var pinSkinRepository: PinSkinRepository

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private var address: String = "주소를 가져오는 중..."
    private var selectedPinColor: PinSkin? = null
    private val hashtagEditRecyclerView: RecyclerView by lazy {
        binding.hashtagRecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.pinMemoImage.setImageURI(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EditPinMemoModalBinding.inflate(inflater, container, false)
        initViews()
        setupListeners()

        binding.root.setOnTouchListener { _, _ ->
            true
        }

        return binding.root
    }

    private fun initViews() {
        binding.memoDateEditButton.load("file:///android_asset/pin/calendar_today.png")
        binding.locationIconEdit.load("file:///android_asset/pin/location_on.png")
        try {
            val pinButtonBg = requireContext().assets.open("pin/pin_button.png").use { Drawable.createFromStream(it, null) }
            binding.pinColorSelectButton.background = pinButtonBg
        } catch (e: IOException) { e.printStackTrace() }

        initDefaultPinSkin()
        loadAddress()
        updateDateText()
        setupHashtagEditRecyclerView()
    }
    private fun setupHashtagEditRecyclerView() {
        hashtagEditRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hashtagEditRecyclerView.adapter = EditableHashtagAdapter()
    }


    private fun setupListeners() {
        binding.pinMemoImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.memoDateEditButton.setOnClickListener {
            showDatePicker()
        }

        binding.pinColorSelectButton.setOnClickListener {
            val dialog = PinSkinChoiceDialogFragment()
            dialog.show(childFragmentManager, "PinSkinDialog")
        }

        childFragmentManager.setFragmentResultListener(
            PinSkinChoiceDialogFragment.REQUEST_KEY,
            viewLifecycleOwner) {
                _, bundle ->
            Log.d("PinMemoInputFragment", "Received pin skin selection result")
            val selected = bundle.getParcelable(PinSkinChoiceDialogFragment.BUNDLE_KEY, PinSkin::class.java)
                ?: return@setFragmentResultListener
            Log.d("PinMemoInputFragment", "Selected pin skin: $selected")
            selectedPinColor = selected
            lifecycleScope.launch {
                binding.pinColorSelectButton.background = pinSkinRepository.getPinSkinDrawableById(selected.id, requireContext())
            }
            Toast.makeText(requireContext(), "${selected.name} 핀이 선택되었습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.memoSaveButton.setOnClickListener {
            saveMemo()
        }
    }

    private fun loadAddress() {
        Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lng, 1) { addresses ->
            address = addresses.firstOrNull()?.getAddressLine(0) ?: "주소를 가져올 수 없습니다."
            binding.pinAddress.text = address
        }
    }

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

    private fun initDefaultPinSkin() {
        lifecycleScope.launch {
            val defaultPinSkin = pinSkinRepository.getPinSkins()[0]
            selectedPinColor = defaultPinSkin
            binding.pinColorSelectButton.background = pinSkinRepository.getPinSkinDrawableById(defaultPinSkin.id, requireContext())
        }
    }

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
            lat,
            lng,
            placeName = binding.pinPlaceName.text.toString(),
            content = binding.memoContentInput.text.toString(),
            date = date,
            imageUri = selectedImageUri!!,
            address = address,
            pinSkinId = selectedPinColor?.id ?: 1L,
            hashtags = (hashtagEditRecyclerView.adapter as EditableHashtagAdapter).getResult()
        )

        onComplete(data)
        close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
