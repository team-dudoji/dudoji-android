package com.dudoji.android.landmark.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.landmark.adapter.LandmarkSearchAdapter
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

class LandmarkSearchActivity : AppCompatActivity() {

    private lateinit var adapter: LandmarkSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_search)

        val searchEditText = findViewById<EditText>(R.id.landmark_search_edit_text)
        val recyclerView = findViewById<RecyclerView>(R.id.friend_recommend_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val initialQuery = intent.getStringExtra("query") ?: ""
        searchEditText.setText(initialQuery)
        searchEditText.requestFocus()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        searchEditText.addTextChangedListener {
            val keyword = it.toString()
            if (keyword.isNotBlank()) {
                searchLandmarksFromServer(keyword)
            }
        }

        if (initialQuery.isNotBlank()) {
            searchLandmarksFromServer(initialQuery)
        }

        adapter = LandmarkSearchAdapter(emptyList()) { selectedLandmark ->
            val intent = Intent(this, LandmarkDetailActivity::class.java)
            intent.putExtra("landmark", selectedLandmark)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

    }

    private fun searchLandmarksFromServer(keyword: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.landmarkApiService.searchLandmarks(keyword)
                if (response.isSuccessful) {
                    val dtoList = response.body() ?: emptyList()
                    val landmarkList = dtoList.map { it.toDomain() }
                    adapter.updateData(landmarkList)
                } else {
                    Toast.makeText(this@LandmarkSearchActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LandmarkSearchActivity, "오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
