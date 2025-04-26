package com.dudoji.android.mypage.achievement.distance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

class DistanceAdapter(private val items: List<DistanceItem>) :
    RecyclerView.Adapter<DistanceAdapter.DistanceViewHolder>() {

    inner class DistanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelText: TextView = itemView.findViewById(R.id.cardLabel)
        val descriptionText: TextView = itemView.findViewById(R.id.cardDescription)

        fun bind(item: DistanceItem) {
            labelText.text = item.label
            descriptionText.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return DistanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DistanceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
