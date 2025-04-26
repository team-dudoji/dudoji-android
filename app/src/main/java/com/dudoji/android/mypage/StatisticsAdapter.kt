package com.dudoji.android.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

data class StatisticItem(
    val label: String,          // 통계 항목의 제목
    val description: String     // 통계 항목의 설명
)

class StatisticsAdapter(private val statistics: List<StatisticItem>) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    inner class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelText: TextView = itemView.findViewById(R.id.cardLabel)
        val descriptionText: TextView = itemView.findViewById(R.id.cardDescription)

        fun bind(statistic: StatisticItem) {
            labelText.text = statistic.label
            descriptionText.text = statistic.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return StatisticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val statistic = statistics[position]
        holder.bind(statistic)
    }

    override fun getItemCount(): Int = statistics.size
}
