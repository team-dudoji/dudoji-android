package com.dudoji.android.map.repository

import android.content.Context
import android.util.Log
import com.dudoji.android.database.dao.MapSectionDao
import com.dudoji.android.map.domain.mapsection.MapSection
import com.dudoji.android.map.manager.MapSectionManager
import com.dudoji.android.map.utils.mapsection.MapSectionParser
import com.dudoji.android.network.api.service.MapApiService

// This repository is responsible for managing the map section data.
object MapSectionRepository {
    lateinit var mapApiService: MapApiService

    // load map sections from the server
    suspend fun loadMapSectionsFromServer(): List<MapSection> {
        if (!::mapApiService.isInitialized) {
            mapApiService = RetrofitClient.mapApiService
            Log.d("MapApiService", "mapApiService initialized in repository")
        }
        val response = mapApiService.getMapSections()
        Log.d("MapApiService", "Response: ${response.message()}")
        Log.d("MapApiService", "Response: ${response.body()}")
        if (response.isSuccessful) {
            val mapSectionList: MutableList<MapSection> = mutableListOf()
            response.body()?.mapSections?.forEach { mapSection ->
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
        } else {
            return emptyList()
//            throw Exception("Failed to load map sections: ${response.message()}")
        }
    }

    // return map section manager
    suspend fun getMapSectionManager(): MapSectionManager {
        return MapSectionManager(loadMapSectionsFromServer())
    }

    // return map section manager
    fun getMapSectionManager(context: Context): MapSectionManager {
        val mapSectionDao = MapSectionDao(context)
        val mapSections = mapSectionDao.getMapSections()
        Log.d("MapSectionDao", "getMapSections: ${mapSections.size}")
        return MapSectionManager(mapSections)
    }
}