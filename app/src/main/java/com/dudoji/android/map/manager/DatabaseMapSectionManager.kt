package com.dudoji.android.map.manager

import android.content.Context
import android.util.Log
import com.dudoji.android.config.BASIC_ZOOM_LEVEL
import com.dudoji.android.database.dao.MapSectionDao
import com.dudoji.android.map.domain.MapSection
import com.dudoji.android.map.domain.RevealCircle
import com.dudoji.android.map.domain.TileCoordinate
import com.dudoji.android.map.repository.RevealCircleRepository
import com.dudoji.android.map.utils.location.IRevealCircleListener
import com.dudoji.android.map.utils.tile.TileCoordinateUtil

class DatabaseMapSectionManager(context: Context): MapSectionManager(), IRevealCircleListener {
    val mapSectionDao = MapSectionDao(context)

    val mapSections = mutableMapOf<TileCoordinate, MapSection>()

    init {
        RevealCircleRepository.revealCircleListenerCaller.addListener(this)
    }

    fun isFogExists(lat: Double, lng: Double): Boolean {
        val pixel = TileCoordinateUtil.Companion.latLngToPixel(lat, lng, BASIC_ZOOM_LEVEL)
        val tile = TileCoordinateUtil.Companion.pixelToTile(pixel.first, pixel.second)
        val tileCoordinate = TileCoordinate(tile.first, tile.second, BASIC_ZOOM_LEVEL)
        val tileXY = TileCoordinateUtil.Companion.pixelToPixelInTile(pixel.first, pixel.second)
        val mapSection = getMapSection(tileCoordinate)
        return mapSection.isFogExists(tileXY.first, tileXY.second)
    }

    override fun getMapSection(coordinate: TileCoordinate): MapSection {
        return mapSections[coordinate] ?:
            mapSectionDao.getMapSection(coordinate.x, coordinate.y).also {
                if (it != null) {
                    mapSections[coordinate] = it
                }
            } ?:
            MapSection.Builder()
            .setXY(coordinate.x, coordinate.y)
            .setBitmap(fullBitmap)
            .build()
    }

    override fun onRevealCircleAdded(revealCircle: RevealCircle) {
        Log.d("DatabaseMapSectionManager", "onRevealCircleAdded: $revealCircle")
        val coordinate = revealCircle.getTileCoordinate()
        val coordinates = TileCoordinateUtil.Companion.getCloseBasicTileCoordinates(coordinate, 1)

        for (coord in coordinates) {
            if (!mapSections.containsKey(coord)) {
                mapSections[coord] = mapSectionDao.getMapSection(coord.x, coord.y) ?:
                MapSection.Builder()
                    .setXY(coord.x, coord.y)
                    .setBitmap(fullBitmap)
                    .build()
            }
            mapSections[coord]!!.applyPosition(
                revealCircle.toWorldPosition()
            )
        }

        mapSections.values.forEach( { mapSection ->
            mapSectionDao.setMapSection(
                mapSection
            )
        })
    }
}