package com.dudoji.android.pin.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.pin.adapter.PinSkinAdapter
import com.dudoji.android.pin.domain.PinSkin

class PinSkinChoiceDialogFragment(val pinSkins: List<PinSkin>, val listener: (PinSkin) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?):Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.pin_select_modal, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.pin_color_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PinSkinAdapter(pinSkins) { selected ->
            listener(selected)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("핀 스킨을 선택하세요")
            .setView(view)
            .setNegativeButton("취소", null)
            .create()
    }
}
