package com.dudoji.android.mypage.achievement.speed

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
import com.dudoji.android.mypage.achievement.time.TimeItem
import com.dudoji.android.repository.AchievementRepository.SpeedRepository

class SpeedActivity : AppCompatActivity() {

    private lateinit var allacievementRecyclerView: RecyclerView
    private lateinit var speedAdapter: SpeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allachievement)

        val title = intent.getStringExtra("CATEGORY_TITLE") ?: "제목 없음"
        findViewById<TextView>(R.id.titleText).text = title

        allacievementRecyclerView = findViewById(R.id.allacievementRecyclerView)

        val speeditems = SpeedRepository.getItems()
        allacievementRecyclerView.layoutManager = GridLayoutManager(this, 2)
        speedAdapter = SpeedAdapter(speeditems)
        allacievementRecyclerView.adapter = speedAdapter

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.addItemButton).setOnClickListener {
            addNewSpeedItem(SpeedItem("새 속도 업적", "방금 추가한 속도입니다!"))
        }
    }
    private fun addNewSpeedItem(item: SpeedItem) {
        SpeedRepository.addItem(item)
        speedAdapter.notifyItemInserted(SpeedRepository.getItems().size - 1)
    }

}