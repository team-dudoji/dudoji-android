package com.dudoji.android.mypage.achievement.speed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

class SpeedAdapter(private val items: List<SpeedItem>) :
    RecyclerView.Adapter<SpeedAdapter.SpeedViewHolder>() {

    inner class SpeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelText: TextView = itemView.findViewById(R.id.cardLabel)
        val descriptionText: TextView = itemView.findViewById(R.id.cardDescription)

        fun bind(item: SpeedItem) {
            labelText.text = item.label
            descriptionText.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return SpeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpeedViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
