package com.dudoji.android.map.utils.mapsection

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import com.dudoji.android.R
import com.dudoji.android.map.domain.MapSection
import com.dudoji.android.map.utils.tile.TILE_SIZE
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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
                builder.setBitmap(createBitmapFromBase64String(bitmap))
            }

            return builder.build()
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun createBitmapFromBase64String(bitmapString: String): Bitmap {
            val byteArray = Base64.decode(bitmapString)
            val bitmap = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888)

            val pixels = IntArray(TILE_SIZE * TILE_SIZE)

            for (y in 0 until TILE_SIZE) {
                for (x in 0 until TILE_SIZE) {
                    val byteIndex = (y * TILE_SIZE + x) / 8
                    val bitIndex = 7 - (x % 8)

                    if (byteIndex >= byteArray.size) {
                        val isFilled = false
                        pixels[y * TILE_SIZE + x] = if (isFilled) Color.BLACK else Color.TRANSPARENT
                    } else {
                        val isFilled = (byteArray[byteIndex].toInt() shr bitIndex) and 1 == 1
                        pixels[y * TILE_SIZE + x] = if (isFilled) Color.BLACK else Color.TRANSPARENT
                    }
                }
            }

            bitmap.setPixels(pixels, 0, TILE_SIZE, 0, 0, TILE_SIZE, TILE_SIZE)

            return bitmap
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