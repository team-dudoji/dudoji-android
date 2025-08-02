package com.dudoji.android.landmark.domain

enum class RouteType(val displayName: String) {
    CAR("차"),
    WALK("도보"),
    TRANSIT("대중교통"),
    BIKE("자전거");

    fun getSelectedIconPath(): String = when (this) {
        CAR -> "route/car_selected.png"
        WALK -> "route/walk_selected.png"
        TRANSIT -> "route/transport_selected.png"
        BIKE -> "route/bike_selected.png"
    }

    fun getUnselectedIconPath(): String = when (this) {
        CAR -> "route/car.png"
        WALK -> "route/walk.png"
        TRANSIT -> "route/transport.png"
        BIKE -> "route/bike.png"
    }
}