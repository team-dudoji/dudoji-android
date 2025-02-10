package com.dudoji.android.util.mapsection

import android.content.res.Resources
import com.dudoji.android.R
import com.dudoji.android.model.mapsection.Bitmap
import com.dudoji.android.model.mapsection.MapSection
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MapSectionParser {

    // static functions in kotlin
    companion object {
        // convert map section json string to base point lng, lat and map sections object
        fun parseMapSections(mapSectionsJsonString: String): Triple<Int, Int, List<MapSection>> {
            var mapSectionsJsonObject = JSONObject(mapSectionsJsonString)
            var mapSectionsJsonArray = mapSectionsJsonObject.getJSONArray("mapSections")
            var mapSections: MutableList<MapSection> = mutableListOf()
            var basePoint = mapSectionsJsonObject.getJSONObject("basePoint")

            for (i in 0 until mapSectionsJsonArray.length()) {
                val mapSectionJsonObject = mapSectionsJsonArray.getJSONObject(i)
                mapSections.add(ParseMapSectionJsonObject(mapSectionJsonObject))
            }
            return Triple(
                basePoint.getInt("lng"),
                basePoint.getInt("lat"),
                mapSections
            )
        }

        // convert map section json object to map section object
        fun ParseMapSectionJsonObject(mapSectionJsonObject: JSONObject): MapSection {
            val x = mapSectionJsonObject.getInt("x")
            val y = mapSectionJsonObject.getInt("y")
            val isExplored = mapSectionJsonObject.getBoolean("explored")
            val builder = MapSection.Builder()
            builder.setXY(x, y)

            if (!isExplored) {
                val bitmap = mapSectionJsonObject.getString("mapData")
                builder.setBitmap(Bitmap(bitmap))
            }

            return builder.build()
        }
    }

    fun testParseMapSections(resources: Resources): List<MapSection> {
        val mapSectionsJsonString : String = getJsonFromRaw(resources, R.raw.map_section_response_test_data)
        val result : Triple<Int, Int, List<MapSection>> = parseMapSections(mapSectionsJsonString)
        return result.third
    }

    fun getJsonFromRaw(resources: Resources, rawResId: Int): String {
        val inputStream = resources.openRawResource(rawResId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.use { it.readText() }
    }
}