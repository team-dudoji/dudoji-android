package com.dudoji.android.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.domain.Quest
import com.dudoji.android.mypage.type.MissionUnit

class DailyQuestAdapter(
    private val quests: List<Quest>
) : RecyclerView.Adapter<DailyQuestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_quest, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = quests.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(quests[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.quest_title)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.quest_progress_bar)
        private val currentValue: TextView = itemView.findViewById(R.id.quest_current_value)
        private val unit: TextView = itemView.findViewById(R.id.quest_unit)
        private val targetValue: TextView = itemView.findViewById(R.id.quest_target_value)
        private val targetUnit: TextView = itemView.findViewById(R.id.quest_target_unit)


        fun bind(quest: Quest) {
            title.text = quest.title

            val progressPercent = if (quest.goalValue == 0) 0 else
                (quest.currentValue.toFloat() / quest.goalValue * 100).toInt()

            progressBar.progress = progressPercent

            currentValue.text = quest.currentValue.toString()
            targetValue.text = quest.goalValue.toString()

            val displayUnit = when (quest.unit) {
                MissionUnit.DISTANCE -> "km"
                MissionUnit.COUNT -> "회"
                MissionUnit.PERCENTAGE -> TODO()
            }
            unit.text = displayUnit
            targetUnit.text = displayUnit
        }
    }
}