package com.dudoji.android.pin.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.domain.model.PinSkin
import com.dudoji.android.domain.repository.PinSkinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class PinSkinAdapter(
    private val pinSkins: List<PinSkin>,
    private val pinSkinRepository: PinSkinRepository,
    private val onItemClick: (PinSkin) -> Unit,
) : RecyclerView.Adapter<PinSkinAdapter.PinSkinViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinSkinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pin_color_item, parent, false)
        return PinSkinViewHolder(view, pinSkinRepository)
    }

    override fun onBindViewHolder(holder: PinSkinViewHolder, position: Int) {
        val pinSkin = pinSkins[position]
        holder.bind(pinSkin)

        holder.itemView.isSelected = (position == selectedPosition)
        holder.itemView.setOnClickListener {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick(pinSkin)
        }
    }

    override fun getItemCount(): Int = pinSkins.size

    class PinSkinViewHolder(
        itemView: View,
        private val pinSkinRepository: PinSkinRepository
    ) : RecyclerView.ViewHolder(itemView) {

        private val pinSkinImage: ImageView = itemView.findViewById(R.id.pin_color_image)
        private val pinSkinName: TextView = itemView.findViewById(R.id.pin_color_name)

        fun bind(pinSkin: PinSkin) {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = pinSkinRepository.getPinSkinBitmapById(pinSkin.id, itemView.context)
                withContext(Dispatchers.Main) {
                    if (bitmap != null) {
                        pinSkinImage.setImageBitmap(bitmap)
                    } else {
                        pinSkinImage.load("file:///android_asset/pin/pin_button.png")
                    }
                }
            }
            pinSkinName.text = pinSkin.name
        }
    }
}