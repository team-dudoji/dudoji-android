package com.dudoji.android.pin.adapter

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.util.WeekTranslator
import java.io.IOException
import java.time.format.DateTimeFormatter

class PinMemoAdapter(
    private var itemList: List<Pin>,
    private val onItemClick: ((Pin) -> Unit)? = null
) : RecyclerView.Adapter<PinMemoAdapter.PinViewHolder>() {

    inner class PinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.pin_place_name)
        val content: TextView = itemView.findViewById(R.id.pin_item_content)
        val image: ImageView = itemView.findViewById(R.id.pin_image)
        val date: TextView = itemView.findViewById(R.id.pin_item_date)
        val likeCount: TextView = itemView.findViewById(R.id.pin_item_like_count)
        val likeIcon: ImageView = itemView.findViewById(R.id.pin_item_like_icon)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(itemList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pin_memo_item, parent, false)
        return PinViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PinViewHolder, position: Int) {
        val pin = itemList[position]
        holder.placeName.text = pin.placeName
        holder.content.text = pin.content

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val formattedDate = "${pin.createdDate.format(formatter)} (${WeekTranslator.translateWeekToKorean(pin.createdDate.dayOfWeek.value)})"
        holder.date.text = formattedDate
        holder.likeCount.text = pin.likeCount.toString()

        holder.likeIcon.load("file:///android_asset/pin/heart_like.png")

        holder.image.load("${RetrofitClient.BASE_URL}/${pin.imageUrl}") {
            crossfade(true)
            error(R.mipmap.photo_placeholder)
            placeholder(R.mipmap.photo_placeholder)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateItems(newItems: List<Pin>) {
        itemList = newItems
        notifyDataSetChanged()
    }

    fun sortBy(type: SortType) {
        itemList = itemList.sortedWith(Comparator(type.comparator))
        notifyDataSetChanged()
    }
}