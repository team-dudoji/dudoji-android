package com.dudoji.android.pin.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.data.datasource.location.LocationService
import com.dudoji.android.pin.domain.Pin
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

enum class SortType(val comparator: (Pin, Pin) -> Int) {

    @RequiresApi(Build.VERSION_CODES.O)
    ENROLL({ p1, p2 -> p1.createdDate.compareTo(p2.createdDate) }),

    POPULAR({ p1, p2 -> p2.likeCount - p1.likeCount }),

    @RequiresApi(Build.VERSION_CODES.O)
    RECENT({ p1, p2 -> p2.createdDate.compareTo(p1.createdDate) }),

    DISTANCE({ p1, p2 ->
//        val (myLat, myLng) = LocationService.getLastLatLng()
//        val myLatLng = LatLng(myLat, myLng)
//        val d1 = SphericalUtil.computeDistanceBetween(myLatLng, LatLng(p1.lat, p1.lng))
//        val d2 = SphericalUtil.computeDistanceBetween(myLatLng, LatLng(p2.lat, p2.lng))
//        d1.compareTo(d2)
        0
    });
}
