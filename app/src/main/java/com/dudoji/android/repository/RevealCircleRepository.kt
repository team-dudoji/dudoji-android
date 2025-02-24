package com.dudoji.android.repository

import android.location.Location
import com.dudoji.android.config.REVEAL_CIRCLE_RADIUS_BY_WALK
import com.dudoji.android.model.RevealCircle
import com.dudoji.android.util.listener.ListenerCaller
import com.dudoji.android.util.location.IRevealCircleListener
import java.util.*

object RevealCircleRepository {

    val revealCircleQueue: Queue<RevealCircle> = LinkedList()
    val revealCircleListenerCaller: ListenerCaller<IRevealCircleListener, RevealCircle> = ListenerCaller()

    fun addLocation(location: Location) {
        val revealCircle = RevealCircle(location.latitude, location.longitude, REVEAL_CIRCLE_RADIUS_BY_WALK)
        synchronized(this) {
            revealCircleQueue.add(revealCircle)
            revealCircleListenerCaller.callListeners(revealCircle)
        }
    }

    fun getLocations() : List<RevealCircle>{
        return revealCircleQueue.toList()
    }
}
