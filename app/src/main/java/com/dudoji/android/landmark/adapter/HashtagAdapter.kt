package com.dudoji.android.landmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

class HashtagAdapter(private val hashtags: List<String>) :
    RecyclerView.Adapter<HashtagAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hashtagText: TextView = view.findViewById(R.id.hashtagText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hashtag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hashtagText.text = hashtags[position]
    }

    override fun getItemCount(): Int = hashtags.size
}
