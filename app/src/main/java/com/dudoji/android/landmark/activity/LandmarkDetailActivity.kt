package com.dudoji.android.landmark.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.landmark.adapter.HashtagAdapter
import com.dudoji.android.landmark.adapter.RouteAdapter
import com.dudoji.android.landmark.domain.Landmark
import com.dudoji.android.landmark.domain.Route
import com.dudoji.android.landmark.domain.RouteType

class LandmarkDetailActivity : AppCompatActivity() {

    private lateinit var routeAdapter: RouteAdapter
    private lateinit var hashtagAdapter: HashtagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        val landmark = intent.getSerializableExtra("landmark") as? Landmark
        if (landmark == null) {
            finish()
            return
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        findViewById<TextView>(R.id.locationTitle).text = landmark.placeName
        findViewById<TextView>(R.id.locationSubtitle).text = landmark.content

        val headerImageView = findViewById<ImageView>(R.id.headerImage)
        val fullImageUrl = "${RetrofitClient.BASE_URL.trimEnd('/')}/${landmark.detailImageUrl.trimStart('/')}"
        headerImageView.load(fullImageUrl) {
            error(R.drawable.campus_image)
            crossfade(true)
        }

        val hashtagRecyclerView = findViewById<RecyclerView>(R.id.hashtagRecyclerView)
        hashtagRecyclerView.layoutManager = GridLayoutManager(this, 3)
        val hashtags = landmark.hashtags.take(3) // 최대 3개만
        hashtagAdapter = HashtagAdapter(hashtags.toMutableList())
        hashtagRecyclerView.adapter = hashtagAdapter

        val transportRecyclerView = findViewById<RecyclerView>(R.id.transportRecyclerView)
        transportRecyclerView.layoutManager = GridLayoutManager(this, 2)

        val routeList = listOf(
            Route(RouteType.CAR, "16분"),
            Route(RouteType.WALK, "30분"),
            Route(RouteType.TRANSIT, "24분"),
            Route(RouteType.BIKE, "18분")
        )

        routeAdapter = RouteAdapter(routeList)
        transportRecyclerView.adapter = routeAdapter
    }
}
