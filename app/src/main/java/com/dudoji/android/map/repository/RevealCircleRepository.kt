package com.dudoji.android.map.repository

import android.content.Context
import android.location.Location
import android.util.Log
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.database.dao.MapSectionDao
import com.dudoji.android.map.domain.RevealCircle
import com.dudoji.android.map.domain.mapsection.DetailedMapSection
import com.dudoji.android.map.domain.mapsection.MapSection
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.utils.location.IRevealCircleListener
import com.dudoji.android.map.utils.mapsection.BitmapUtil
import com.dudoji.android.map.utils.tile.mask.IMaskTileMaker
import com.dudoji.android.network.dto.revealcircle.RevealCircleRequest
import com.dudoji.android.util.listener.ListenerCaller
import java.util.*

// This repository is responsible for managing the reveal circle data.
object RevealCircleRepository {
    private const val TAG = "RevealCircleRepository_Database"
    val mapApiService = RetrofitClient.mapApiService

    val revealCircleQueue: Queue<RevealCircle> = LinkedList()
    val revealCircleListenerCaller: ListenerCaller<IRevealCircleListener, RevealCircle> = ListenerCaller()

    // Add a listener to the listener caller
    fun addLocation(location: Location) {
        val revealCircle = RevealCircle(location.latitude, location.longitude, REVEAL_CIRCLE_RADIUS_BY_WALK)
        synchronized(this) {
            revealCircleQueue.add(revealCircle)
            revealCircleListenerCaller.callListeners(revealCircle)
        }
    }

    // return the list of reveal circles
    fun getLocations() : List<RevealCircle>{
        return revealCircleQueue.toList()
    }

    // save reveal circles to the server
    @Deprecated("This method is deprecated, use saveMapSections instead.")
    suspend fun saveRevealCirclesToServer() {
        val revealCircles: RevealCircleRequest = RevealCircleRequest(
            revealCircles = getLocations()
        )
        val response = mapApiService.saveCircle(revealCircles)
        if (response.isSuccessful) {
            Log.d(TAG, "Saved reveal circles: ${revealCircles.revealCircles}")
        } else {
            Log.e(TAG, "Failed to save reveal circles: ${response.message()}")
        }
    }

    suspend fun saveRevealCirclesToDatabase(context: Context, mapSectionManager: MapSectionManager, maskTileMaker: IMaskTileMaker) {
        val dirtyMapSections = mapSectionManager.getDirtyMapSections()
        val resultMapSections = mutableListOf<MapSection>()

        val mapSectionDao = MapSectionDao(context)
        for (mapSection in dirtyMapSections) {
            if (mapSection is DetailedMapSection) {
                val bitmap = maskTileMaker.createMaskTile(mapSection.x, mapSection.y, BASIC_ZOOM_LEVEL)
                mapSection.setBitmap(bitmap)

                val transparencyRatio = BitmapUtil.calculateTransparencyRatio(bitmap)
                if (transparencyRatio > 0.8) {
                    resultMapSections.add(MapSection(mapSection))
                } else {
                    resultMapSections.add(mapSection)
                }
            }
        }

        for (mapSection in resultMapSections) {
            if (mapSection is DetailedMapSection) {
                mapSectionDao.setMapSection(mapSection)
            }
        }
    }
}
