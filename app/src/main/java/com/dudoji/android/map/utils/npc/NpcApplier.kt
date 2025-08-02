package com.dudoji.android.map.utils.npc

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.R
import com.dudoji.android.map.activity.MapActivity
import com.dudoji.android.map.activity.MapActivity.ActivityMapObject
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.map.repository.NpcDataSource
import com.dudoji.android.map.utils.NonClusterMarkerApplier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMarkerLoaded(marker: Marker?) {
        val npc = marker?.tag as? Npc ?: return
        if (npc.hasQuest) {
            val initialBitmap = BitmapFactory.decodeResource(activity.resources, R.mipmap.quest_bubble)
            val activityMapObject = ActivityMapObject(LatLng(npc.lat, npc.lng), initialBitmap, 70f, -150f, 170, -100)
            npc.activityMapObject = activityMapObject
            activity.activityObjects.add(activityMapObject)
        }
    }
}