package com.dudoji.android.pin.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.pin.domain.Pin
import com.dudoji.android.pin.repository.PinRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class PinApplier(val clusterManager: ClusterManager<Pin>,
                 val googleMap: GoogleMap,
                 val activity: AppCompatActivity,
                 val pinFilter: PinFilter
): OnCameraIdleListener {

    private var hasToReload = false

    companion object {
        private val appliedPins: HashSet<Pin> = HashSet()
    }
    init {
        clusterManager.setOnClusterItemClickListener{
                pin ->
            PinModal.openPinMemoModal(activity, pin)
            true
        }
        clusterManager.setOnClusterClickListener {
            if (it.size >= 1) {
                Log.d("PinApplier", "onClusterClick: $it")
                PinModal.openPinMemosModal(activity, it.items.toList())
            }
            true
        }
    }


    fun applyPins(pins: List<Pin>) {
        val filteredPins = pinFilter.filterPins(pins)

        filteredPins.forEach { pin ->
            if (!appliedPins.contains(pin)) {
                clusterManager.addItem(pin)
                appliedPins.add(pin)
            }
        }
        clusterManager.cluster()
    }

    fun markForReload() {
        hasToReload = true
    }

    fun clearPins() {
        appliedPins.clear()
        clusterManager.clearItems()
        clusterManager.cluster()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCameraIdle() {
        activity.lifecycleScope.launch {
            if (hasToReload || PinRepository.load(googleMap.projection.visibleRegion.latLngBounds.center, 100.0)) {
                val pins = PinRepository.getPins()
                clearPins()
                applyPins(pins)
            }
            hasToReload = false
        }
    }
}