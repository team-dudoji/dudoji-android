package com.dudoji.android.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.domain.MissionUnit

class AchievementAdapter(
    private val achievements: List<Achievement>
) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = achievements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.achievement_title)
        private val value: TextView = itemView.findViewById(R.id.achievement_value)
        private val unit: TextView = itemView.findViewById(R.id.achievement_unit)

        fun bind(achievement: Achievement) {
            title.text = achievement.title

            value.text = achievement.value.toString()

            when (achievement.unit) {
                MissionUnit.PERCENTAGE -> unit.text = "%"
                MissionUnit.DISTANCE -> unit.text = "km"
                MissionUnit.COUNT -> unit.text = "회"
            }
        }
    }
}
