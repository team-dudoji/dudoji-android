package com.dudoji.android.util.mapsection

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dudoji.android.R
import com.dudoji.android.model.mapsection.MapSection
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStreamReader

@RunWith(AndroidJUnit4::class)
class MapSectionParserTester {
    @Test
    fun testParseMapSections() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val mapSectionsJsonString : String = getJsonFromRaw(context, R.raw.map_section_response_test_data)

        val result : Triple<Int, Int, List<MapSection>> = MapSectionParser.parseMapSections(mapSectionsJsonString)

        assertEquals(129082255, result.first)
        assertEquals(35230853, result.second)
        assertEquals(2, result.third.size)
        
        print("successfully map sections parsed")

    }

    fun getJsonFromRaw(context: android.content.Context, rawResId: Int): String {
        val inputStream = context.resources.openRawResource(rawResId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.use { it.readText() }
    }
}
