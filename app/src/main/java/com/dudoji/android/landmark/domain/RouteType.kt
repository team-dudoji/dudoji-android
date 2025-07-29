package com.dudoji.android.landmark.domain

import com.dudoji.android.R

enum class RouteType(val displayName: String) {
    CAR("차"),
    WALK("도보"),
    TRANSIT("대중교통"),
    BIKE("자전거");

    fun getSelectedIconRes(): Int = when (this) {
        CAR -> R.drawable.car_selected
        WALK -> R.drawable.walk_selected
        TRANSIT -> R.drawable.transport_selected
        BIKE -> R.drawable.bike_selected
    }

    fun getUnselectedIconRes(): Int = when (this) {
        CAR -> R.drawable.car
        WALK -> R.drawable.walk
        TRANSIT -> R.drawable.transport
        BIKE -> R.drawable.bike
    }
}