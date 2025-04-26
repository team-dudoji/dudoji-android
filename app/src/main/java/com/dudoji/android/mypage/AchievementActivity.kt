package com.dudoji.android.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

class AchievementActivity : AppCompatActivity() {

    private lateinit var categoriesRecyclerView: RecyclerView //리사이클뷰 변수
    private lateinit var achievementAdapter: AchievementAdapter //리사이클뷰 데이터 넣어버리는 클래스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)

        // 데이터 하드로 박음
        val categories = listOf(
            Category(
                title = "이동거리",
                items = listOf(
                    CategoryItem("하루 5km", "하루\n5km 주파!"),
                    CategoryItem("하루 3km", "하루\n3km 주파!"),
                    CategoryItem("하루 1km", "하루\n1km 주파!")
                )
            ),
            Category(
                title = "시간",
                items = listOf(
                    CategoryItem("하루 2시간", "하루\n2시간 운동!"),
                    CategoryItem("하루 1시간", "하루\n1시간 운동!")
                )
            ),
            Category(
                title = "속도",
                items = listOf(
                    CategoryItem("최고 속도 12km/h", "하루\n최고속도 12km/h!"),
                    CategoryItem("최고 속도 10km/h", "하루\n최고속도 10km/h!")
                )
            )
        )

        // 리사이클 뷰 항목 배치, LinearLayoutManager는 세로 리스트로 배치한다네요
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        achievementAdapter = AchievementAdapter(categories) //AchievementAdapter에 데이터 넣음
        categoriesRecyclerView.adapter = achievementAdapter //리사이클뷰에 어뎁터 연결하여 화면 표시 준비 갈 완료

        //백버튼 기능
        val backButton = findViewById<View>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }
    }
}
