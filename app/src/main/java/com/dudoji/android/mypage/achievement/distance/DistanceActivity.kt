package com.dudoji.android.mypage.achievement.distance

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.repository.AchievementRepository.DistanceRepository

class DistanceActivity : AppCompatActivity() {

    private lateinit var allacievementRecyclerView: RecyclerView
    private lateinit var distanceAdapter: DistanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allachievement)

        val title = intent.getStringExtra("CATEGORY_TITLE") ?: "이동 거리"
        findViewById<TextView>(R.id.titleText).text = title

        allacievementRecyclerView = findViewById(R.id.allacievementRecyclerView)

        val distanceitems = DistanceRepository.getItems() //  repository에서 가져옴
        distanceAdapter = DistanceAdapter(distanceitems) // toMutableList()로 복사본 생성
        allacievementRecyclerView.layoutManager = GridLayoutManager(this, 2)
        allacievementRecyclerView.adapter = distanceAdapter

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.addItemButton).setOnClickListener {
            addNewDistanceItem(DistanceItem("새 이동거리", "그리샤 예거"))
        }
    }

    private fun addNewDistanceItem(item: DistanceItem) {
        DistanceRepository.addItem(item) // repository에 추가
        distanceAdapter.notifyItemInserted(DistanceRepository.getItems().size - 1) // 반영
    }
}
