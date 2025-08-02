package com.dudoji.android.landmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dudoji.android.R
import com.dudoji.android.landmark.domain.Route

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
        holder.title.text = route.type.displayName
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

        val iconPath = if (isSelected) {
            route.type.getSelectedIconPath()
        } else {
            route.type.getUnselectedIconPath()
        }
        holder.icon.load("file:///android_asset/$iconPath")

        holder.container.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener

            val previous = selectedPosition
            selectedPosition = currentPos
            notifyItemChanged(previous)
            notifyItemChanged(currentPos)
        }

    }

    override fun getItemCount(): Int = routes.size

}
