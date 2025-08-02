package com.dudoji.android.map.utils.npc

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.map.repository.NpcDataSource
import com.dudoji.android.map.utils.NonClusterMarkerApplier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.collections.MarkerManager

class NpcApplier(normalMarkerCollection: MarkerManager.Collection, googleMap: GoogleMap, activity: MapActivity)
    : NonClusterMarkerApplier<Npc>(normalMarkerCollection, googleMap, activity), OnCameraIdleListener{

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(
        latLng: LatLng,
        radius: Double
    ): Boolean {
        return NpcDataSource.load(latLng, radius)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getMarkerBases(): List<Npc> {
        return NpcDataSource.getNpcs()
    }
}