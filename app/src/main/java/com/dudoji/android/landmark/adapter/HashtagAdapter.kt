package com.dudoji.android.landmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R

open class HashtagAdapter(protected val hashtags: MutableList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hashtagText: TextView = view.findViewById(R.id.hashtagText)
        fun bind(text: String) {
            hashtagText.text = "#$text"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hashtag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as ViewHolder
        holder.bind(hashtags[position])
    }

    override fun getItemCount(): Int = hashtags.size
}
