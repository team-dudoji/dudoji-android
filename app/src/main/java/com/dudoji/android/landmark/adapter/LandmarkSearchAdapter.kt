package com.dudoji.android.landmark.adapter

import RetrofitClient
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.dudoji.android.R
import com.dudoji.android.landmark.domain.Landmark
import java.lang.Exception

class LandmarkSearchAdapter(
    private var fullList: List<Landmark>,
    private val onItemClick: (Landmark) -> Unit
) : RecyclerView.Adapter<LandmarkSearchAdapter.ViewHolder>() {

    private var filteredList: List<Landmark> = fullList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val festivalBorder: View = itemView.findViewById(R.id.festival_border)
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val nameText: TextView = itemView.findViewById(R.id.title_text)
        val festivalTag: ImageView = itemView.findViewById(R.id.festival_tag)
        val hashtagText: TextView = itemView.findViewById(R.id.hashtag_text)

        fun bind(landmark: Landmark) {
            nameText.text = landmark.placeName
            hashtagText.text = landmark.hashtags.take(3).joinToString(" ") { "#$it" }

            val defaultDrawable = try {
                itemView.context.assets.open("landmark/default_landmark.png").use { inputStream ->
                    Drawable.createFromStream(inputStream, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            profileImage.load("${RetrofitClient.BASE_URL}/${landmark.detailImageUrl}") {
                crossfade(true)
                placeholder(defaultDrawable)
                error(defaultDrawable)
                transformations(CircleCropTransformation())
            }

            val visibility = if (landmark.isFestival) View.VISIBLE else View.GONE
            festivalBorder.visibility = visibility
            festivalTag.visibility = visibility

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(landmark)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.landmark_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    fun updateData(newList: List<Landmark>) {
        fullList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}