package com.dudoji.android.map.manager

import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.domain.mapsection.MapSection

class MemoryMapSectionManager(mapSections: List<MapSection>) : MapSectionManager() {
    private val mapSections: Map<TileCoordinate, MapSection>
    override fun getMapSection(coordinate: TileCoordinate): MapSection? {
        return mapSections[coordinate]
    }

    init {
        this.mapSections = mapSections.associateBy {
            TileCoordinate(it.x, it.y, 15)
        }
    }
}