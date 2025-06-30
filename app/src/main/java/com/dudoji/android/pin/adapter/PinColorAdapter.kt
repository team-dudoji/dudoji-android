package com.dudoji.android.pin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.pin.color.PinColor

class PinColorAdapter(
    private val pinColors: List<PinColor>,
    private val onItemClick: (PinColor) -> Unit
) : RecyclerView.Adapter<PinColorAdapter.PinColorViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pin_color_item, parent, false)
        return PinColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: PinColorViewHolder, position: Int) {
        val pinColor = pinColors[position]
        holder.bind(pinColor)

        holder.itemView.isSelected = (position == selectedPosition)
        holder.itemView.setOnClickListener {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick(pinColor)
        }
    }

    override fun getItemCount(): Int = pinColors.size

    fun setSelectedColor(pinColorId: Int) {
        val index = pinColors.indexOfFirst { it.id == pinColorId }
        if (index != -1) {
            val oldSelectedPosition = selectedPosition
            selectedPosition = index
            if (oldSelectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldSelectedPosition)
            }
            notifyItemChanged(selectedPosition)
        }
    }

    class PinColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pinColorImage: ImageView = itemView.findViewById(R.id.pin_color_image)
        private val pinColorName: TextView = itemView.findViewById(R.id.pin_color_name)

        fun bind(pinColor: PinColor) {
            pinColorImage.setImageResource(pinColor.imageResId)
            pinColorName.text = pinColor.name
        }
    }
}