package com.dudoji.android.mypage.achievement.time

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.mypage.StatisticItem
import com.dudoji.android.mypage.StatisticsAdapter
import com.dudoji.android.mypage.achievement.distance.DistanceItem
import com.dudoji.android.repository.AchievementRepository.TimeRepository

class TimeActivity : AppCompatActivity() {

    private lateinit var allacievementRecyclerView: RecyclerView
    private lateinit var timeAdapter: TimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allachievement)

        val title = intent.getStringExtra("CATEGORY_TITLE") ?: "제목 없음"
        findViewById<TextView>(R.id.titleText).text = title

        allacievementRecyclerView = findViewById(R.id.allacievementRecyclerView)

        val items = TimeRepository.getItems()
        allacievementRecyclerView.layoutManager = GridLayoutManager(this, 2)
        timeAdapter = TimeAdapter(items)
        allacievementRecyclerView.adapter = timeAdapter

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.addItemButton).setOnClickListener {
            addNewTimeItem(TimeItem("새 시간 업적", "지크 예거"))
        }

    }

    private fun addNewTimeItem(item: TimeItem) {
        TimeRepository.addItem(item)
        timeAdapter.notifyItemInserted(TimeRepository.getItems().size - 1)
    }

}
