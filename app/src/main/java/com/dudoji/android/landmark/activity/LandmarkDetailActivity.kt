package com.dudoji.android.landmark.activity

import android.graphics.drawable.Drawable
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

        val backButton: ImageButton = findViewById(R.id.backButton)

        try {
            val inputStream = assets.open("landmark/back_button.png")
            val drawable = Drawable.createFromStream(inputStream, null)
            backButton.setImageDrawable(drawable)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        backButton.setOnClickListener { finish() }

        findViewById<TextView>(R.id.locationTitle).text = landmark.placeName
        findViewById<TextView>(R.id.locationSubtitle).text = landmark.content

        val headerImageView = findViewById<ImageView>(R.id.headerImage)

        val placeholderDrawable = try {
            assets.open("landmark/campus_image.png").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // 파일을 찾지 못할 경우 null
        }

        val fullImageUrl = "${RetrofitClient.BASE_URL.trimEnd('/')}/${landmark.detailImageUrl.trimStart('/')}"
        headerImageView.load(fullImageUrl) {
            crossfade(true)
            placeholder(placeholderDrawable)
            error(placeholderDrawable)
        }
        val hashtagRecyclerView = findViewById<RecyclerView>(R.id.hashtagRecyclerView)
        hashtagRecyclerView.layoutManager = GridLayoutManager(this, 3)
        val hashtags = landmark.hashtags.take(3)
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