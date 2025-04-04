package com.dudoji.android.repository

import android.location.Location
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.model.RevealCircle
import com.dudoji.android.network.entity.revealcircle.RevealCircleRequest
import com.dudoji.android.util.listener.ListenerCaller
import com.dudoji.android.util.location.IRevealCircleListener
import java.util.*

// This repository is responsible for managing the reveal circle data.
object RevealCircleRepository {
    val mapApiService = RetrofitClient.mapApiService

    val revealCircleQueue: Queue<RevealCircle> = LinkedList()
    val revealCircleListenerCaller: ListenerCaller<IRevealCircleListener, RevealCircle> = ListenerCaller()

    // Add a listener to the listener caller
    fun addLocation(location: Location) {
        val revealCircle = RevealCircle(location.latitude, location.longitude, REVEAL_CIRCLE_RADIUS_BY_WALK)
        synchronized(this) {
            revealCircleQueue.add(revealCircle)
            revealCircleListenerCaller.callListeners(revealCircle)
        }
    }

    // return the list of reveal circles
    fun getLocations() : List<RevealCircle>{
        return revealCircleQueue.toList()
    }

    // save reveal circles to the server
    suspend fun saveRevealCircles() {
        val revealCircles: RevealCircleRequest = RevealCircleRequest(
            revealCircles = getLocations()
        )
        mapApiService.saveCircle(revealCircles)
    }
}
