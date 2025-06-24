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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.dudoji.android.R
import com.dudoji.android.pin.util.PinMakeData
import com.dudoji.android.util.WeekTranslator
import com.dudoji.android.util.modal.ModalFragment
import java.time.LocalDate
import java.util.Locale

class PinMemoInputFragment(
    val lat: Double,
    val lng: Double,
    private val onComplete: (PinMakeData) -> Unit
): ModalFragment() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private var address: String = "주소를 가져오는 중..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = uri
                view?.findViewById<ImageView>(R.id.pin_memo_image)?.setImageURI(uri)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.edit_pin_memo_modal, container, false)

        val pinContent = view.findViewById<EditText>(R.id.memo_content_input)
        val pinDate = view.findViewById<TextView>(R.id.memo_date)
        val imageView = view.findViewById<ImageView>(R.id.pin_memo_image)
        val pinDateEditButton = view.findViewById<ImageView>(R.id.memo_date_edit_button)
        val saveButton = view.findViewById<Button>(R.id.memo_save_button)
        val placeName = view.findViewById<EditText>(R.id.pin_place_name)
        val pinAddress = view.findViewById<TextView>(R.id.pin_address)

        Geocoder(requireContext(), Locale.getDefault()).getFromLocation(
            lat,
            lng,
            1
        ){ addresses ->
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0) ?: "주소를 가져올 수 없습니다."
                pinAddress.text = address
            } else {
                pinAddress.text = "주소를 가져올 수 없습니다."
            }
        }

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = "$year.${month + 1}.$dayOfMonth (${WeekTranslator.translateWeekToKorean(calendar.get(Calendar.DAY_OF_WEEK))})"
                pinDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7 // 7 days ago

        imageView.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        pinDateEditButton.setOnClickListener {
            datePicker.show()
        }

        saveButton.setOnClickListener {
            val content = pinContent.text.toString()
            val date = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onComplete(
                PinMakeData(
                    placeName.text.toString(), content, date, selectedImageUri!!, address
                )
            )

            close()
        }

        return view
    }
}