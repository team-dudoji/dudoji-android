package com.dudoji.android.map.utils.location

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dudoji.android.map.utils.location.LocationService.Companion.lastLocation
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.launch

class DemoLocationService(val activity: AppCompatActivity): LocationService {

    var googleMap: GoogleMap? = null
    val simulatedLocations = mutableListOf<Location>(
        Location("dummy_provider").apply {
            latitude = 0.0
            longitude = 0.0
        }
    )

    override fun setLocationCallback(callback: (LocationResult?) -> Unit) {
        activity.lifecycleScope.launch {
            while (true) {
                if (googleMap == null) {
                    kotlinx.coroutines.delay(1000L)
                    continue
                }
                val position = googleMap!!.cameraPosition
                lastLocation = simulatedLocations.last()
                simulatedLocations.add(
                    Location("dummy_provider").apply {
                        latitude = position.target.latitude
                        longitude = position.target.longitude
                    }
                )
                callback(LocationResult.create(simulatedLocations))
                kotlinx.coroutines.delay(1000L)
            }
        }
    }
}