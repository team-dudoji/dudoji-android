package com.dudoji.android.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.domain.Landmark
import com.dudoji.android.mypage.type.LandmarkType

class LandmarkAdapter(
    private val landmarks: List<Landmark>
) : RecyclerView.Adapter<LandmarkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_landmark, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = landmarks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(landmarks[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.landmark_title)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.landmark_progress_bar)
        private val currentValue: TextView = itemView.findViewById(R.id.landmark_current_value)
        private val unit: TextView = itemView.findViewById(R.id.landmark_unit)
        private val targetValue: TextView = itemView.findViewById(R.id.landmark_target_value)
        private val targetUnit: TextView = itemView.findViewById(R.id.landmark_target_unit)

        fun bind(landmark: Landmark) {
            title.text = landmark.title

            val progressPercent = if (landmark.goalValue == 0) 0 else
                (landmark.currentValue.toFloat() / landmark.goalValue * 100).toInt()

            progressBar.progress = progressPercent

            currentValue.text = landmark.currentValue.toString()
            targetValue.text = landmark.goalValue.toString()

            when (landmark.type) {
                LandmarkType.DISTANCE -> {
                    unit.text = "km"
                    targetUnit.text = "km"
                }
                LandmarkType.COUNT -> {
                    unit.text = "회"
                    targetUnit.text = "회"
                }
            }
        }
    }
}
