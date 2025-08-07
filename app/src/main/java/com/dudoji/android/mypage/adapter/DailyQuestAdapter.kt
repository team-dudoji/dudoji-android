package com.dudoji.android.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.domain.MissionUnit
import com.dudoji.android.mypage.domain.Quest

open class DailyQuestAdapter(
    val quests: List<Quest>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_quest, parent, false)
        return DailyQuestViewHolder(view)
    }

    override fun getItemCount(): Int = quests.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questHolder = holder
        if (questHolder is DailyQuestViewHolder) {
            questHolder.bind(quests[position])
        }
    }

    open class DailyQuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.quest_title)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.quest_progress_bar)
        private val currentValue: TextView = itemView.findViewById(R.id.quest_current_value)
        private val unit: TextView = itemView.findViewById(R.id.quest_unit)
        private val targetValue: TextView = itemView.findViewById(R.id.quest_target_value)
        private val targetUnit: TextView = itemView.findViewById(R.id.quest_target_unit)

        open fun bind(quest: Quest) {
            title.text = quest.title

            val progressPercent = if (quest.goalValue == 0) 0 else
                (quest.currentValue.toFloat() / quest.goalValue * 100).toInt()

            progressBar.progress = progressPercent

            currentValue.text = quest.currentValue.toString()
            targetValue.text = quest.goalValue.toString()

            val displayUnit = when (quest.unit) {
                MissionUnit.DISTANCE -> "km"
                MissionUnit.COUNT -> "회"
                MissionUnit.PERCENTAGE -> "%"
            }
            unit.text = displayUnit
            targetUnit.text = displayUnit
        }
    }
}