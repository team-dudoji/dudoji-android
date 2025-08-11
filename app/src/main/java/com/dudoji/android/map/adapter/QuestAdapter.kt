package com.dudoji.android.map.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.adapter.DailyQuestAdapter
import com.dudoji.android.mypage.adapter.DailyQuestAdapter.DailyQuestViewHolder
import com.dudoji.android.mypage.domain.Quest

class QuestAdapter(quests: List<Quest>) :
    DailyQuestAdapter(quests) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quest_item, parent, false)
        return QuestViewHolder(view)
    }

    override fun getItemCount(): Int = quests.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is QuestViewHolder) {
            throw IllegalStateException("ViewHolder must be of type ViewHolder")
        }
        holder.bind(quests[position])
    }

    class QuestViewHolder(itemView: View) : DailyQuestViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.quest_title)

        override fun bind(quest: Quest) {
            super.bind(quest)
            title.text = quest.title
        }
    }
}