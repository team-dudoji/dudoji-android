package com.dudoji.android.landmark.adapter

import RetrofitClient
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

class LandmarkSearchAdapter(
    private var fullList: List<Landmark>,
    private val onItemClick: (Landmark) -> Unit 
) : RecyclerView.Adapter<LandmarkSearchAdapter.ViewHolder>() {

    private var filteredList: List<Landmark> = fullList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val nameText: TextView = itemView.findViewById(R.id.title_text)
        val hashtagText: TextView = itemView.findViewById(R.id.hashtag_text)

        fun bind(landmark: Landmark) {
            nameText.text = landmark.placeName
            hashtagText.text = landmark.hashtags.take(3).joinToString(" ") { "#$it" }

            profileImage.load("${RetrofitClient.BASE_URL}/${landmark.detailImageUrl}") {
                crossfade(true)
                error(R.drawable.user_placeholder)
                placeholder(R.drawable.user_placeholder)
                transformations(CircleCropTransformation())
            }

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
