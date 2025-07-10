package com.dudoji.android.map.repository

import android.content.Context
import com.dudoji.android.map.manager.DatabaseMapSectionManager
import com.dudoji.android.map.manager.MapSectionManager

// This repository is responsible for managing the map section data.
object MapSectionRepository {
    // return map section manager
    fun getMapSectionManager(context: Context): MapSectionManager {
        return DatabaseMapSectionManager(context)
    }
}