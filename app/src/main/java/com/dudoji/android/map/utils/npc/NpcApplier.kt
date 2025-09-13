package com.dudoji.android.map.utils.npc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.presentation.map.NonClusterMarkerApplier
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.collections.MarkerManager

@RequiresApi(Build.VERSION_CODES.O)
class NpcApplier(
    normalMarkerCollection: MarkerManager.Collection,
    context: Context,
    val onNpcLoaded: (Npc) -> Unit,
    val reset: () -> Unit
    ): NonClusterMarkerApplier<Npc>(normalMarkerCollection, context) {

    init {
//        isIncludedBaseUrl = true
    }

    fun reload() {
        reset()
//        val npcs = NpcDataSource.getNpcs()
//        npcs.forEach { npc ->
//            onNpcLoaded(npc)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMarkerLoaded(marker: Marker?) {
        val npc = marker?.tag as? Npc ?: return
        onNpcLoaded(npc)
    }
}