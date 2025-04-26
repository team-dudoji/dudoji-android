package com.dudoji.android.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudoji.android.R
import androidx.recyclerview.widget.RecyclerView

class StatisticsActivity : AppCompatActivity() {

    private lateinit var statsRecyclerView: RecyclerView
    private lateinit var statisticsAdapter: StatisticsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        statsRecyclerView = findViewById(R.id.statsRecyclerView)

        val categoryTitle = intent.getStringExtra("CATEGORY_TITLE") ?: "없음"
        Log.d("카테고리 확인", "받은 값: $categoryTitle")

        val stats = listOf(
            StatisticItem("총 이동거리", "하루\n5km 주파!"),
            StatisticItem("오늘 이동 거리", "하루\n3km 주파!"),
            StatisticItem("최대 이동 거리", "10 km"),
            StatisticItem("최고 속도", "12 km/h"),
            StatisticItem("최대 이동 거리", "10 km"),
            StatisticItem("최고 속도", "12 km/h")
        )


        // 리사이클 뷰 항목 배치, 그리드레이웃으로 2열로 만들어버렸
        val gridLayoutManager = GridLayoutManager(this, 2) // 2열 그리드
        statsRecyclerView.layoutManager = gridLayoutManager

        // 어댑터 설정
        statisticsAdapter = StatisticsAdapter(stats) //AchievementAdapter에 하드 코딩 데이터 박음
        statsRecyclerView.adapter = statisticsAdapter //리사이클뷰에 어뎁터 연결하여 화면 표시 준비 갈 완료

        // 뒤로가기 버튼 스컬~
        val backButton = findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

