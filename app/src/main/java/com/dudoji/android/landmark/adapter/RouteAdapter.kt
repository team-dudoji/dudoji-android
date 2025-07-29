package com.dudoji.android.landmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dudoji.android.R
import com.dudoji.android.landmark.model.Route

class RouteAdapter(private var routes: List<Route>) :
    RecyclerView.Adapter<RouteAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: RelativeLayout = view.findViewById(R.id.transportContainer)
        val title: TextView = view.findViewById(R.id.routeTitle)
        val time: TextView = view.findViewById(R.id.routeTime)
        val icon: ImageView = view.findViewById(R.id.routeIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = routes[position]
        holder.title.text = route.type
        holder.time.text = route.time

        val isSelected = position == selectedPosition

        holder.title.setTextColor(
            if (isSelected) holder.itemView.context.getColor(R.color.white)
            else holder.itemView.context.getColor(R.color.black)
        )

        holder.time.setTextColor(
            if (isSelected) holder.itemView.context.getColor(R.color.white)
            else holder.itemView.context.getColor(R.color.gray)
        )

        holder.container.setBackgroundResource(
            if (isSelected) R.drawable.route_card_highlighted
            else R.drawable.route_card_background
        )

        val iconRes = when (route.type) {
            "차량" -> if (isSelected) R.drawable.car_selected else R.drawable.car
            "도보" -> if (isSelected) R.drawable.walk_selected else R.drawable.walk
            "대중교통" -> if (isSelected) R.drawable.transport_selected else R.drawable.transport
            "자전거" -> if (isSelected) R.drawable.bike_selected else R.drawable.bike
            else -> R.drawable.dudoji_logo
        }
        holder.icon.setImageResource(iconRes)

        holder.container.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = position
            notifyItemChanged(previous)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = routes.size

}
