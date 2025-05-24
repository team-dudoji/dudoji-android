package com.dudoji.android.pin.fragment

import android.app.DatePickerDialog
import android.icu.util.Calendar
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
import com.dudoji.android.util.WeekTranslator
import com.dudoji.android.util.modal.ModalFragment
import java.time.LocalDate

class PinMemoInputFragment(
    private val onComplete: (Triple<String, LocalDate, Uri?>) -> Unit
): ModalFragment() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()

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

            onComplete(Triple(content, date, selectedImageUri))

            close()
        }

        return view
    }
}