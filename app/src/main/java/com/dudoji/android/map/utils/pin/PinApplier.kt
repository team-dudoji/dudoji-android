package com.dudoji.android.map.utils.pin

import com.dudoji.android.map.domain.MarkerTag
import com.dudoji.android.map.domain.MarkerType
import com.dudoji.android.map.domain.Pin
import com.google.android.gms.maps.GoogleMap


class PinApplier(val googleMap: GoogleMap) {
    companion object {
        private val appliedPins: HashSet<Pin> = HashSet()
    }

    fun applyPin(pin: Pin) {
        if (!appliedPins.contains(pin)) {
            val markerOptions = com.google.android.gms.maps.model.MarkerOptions()
                .position(com.google.android.gms.maps.model.LatLng(pin.lat, pin.lng))
                .title(pin.title)
                .snippet(pin.content)

            val marker = googleMap.addMarker(markerOptions)
            marker.tag = MarkerTag(MarkerType.PIN, pin)

            appliedPins.add(pin)
        }
    }

    fun applyPins(pins: List<Pin>) {
        pins.forEach { pin ->
            applyPin(pin)
        }
    }
}