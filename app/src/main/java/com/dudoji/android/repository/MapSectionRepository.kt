package com.dudoji.android.repository

import com.dudoji.android.model.MapSectionManager
import com.dudoji.android.model.mapsection.MapSection
import com.dudoji.android.network.entity.MapSectionResponse
import com.dudoji.android.util.mapsection.MapSectionParser

// This repository is responsible for managing the map section data.
object MapSectionRepository {
    val mapApiService = RetrofitClient.mapApiService

    // load map sections from the server
    suspend fun loadMapSections(): List<MapSection> {
        val response: MapSectionResponse = mapApiService.getMapSections()
        val mapSectionList: MutableList<MapSection> = mutableListOf()
        response.mapSections.forEach { mapSection ->
            mapSectionList.add(
                MapSection.Builder()
                    .setXY(mapSection.x, mapSection.y)
                    .setBitmap(
                        MapSectionParser.createBitmapFromBase64String(
                            mapSection.mapData!!
                        )
                    )
                    .build()
            )
        }
        return mapSectionList
    }

    // return map section manager
    suspend fun getMapSectionManager(): MapSectionManager {
        return MapSectionManager(loadMapSections())
    }
}