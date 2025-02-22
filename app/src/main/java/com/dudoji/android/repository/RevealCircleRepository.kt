package com.dudoji.android.repository

import android.location.Location
import com.dudoji.android.model.RevealCircle
import com.dudoji.android.util.listener.ListenerCaller
import com.dudoji.android.util.location.IRevealCircleListener
import java.util.*

const val LOCATION_SYSTEM_CHANGE_WARNING_TEXT = "Changed To Different Location System"

const val MAX_LOG_SIZE = 20 //20개로 설정했으니 여기도 해줌

object RevealCircleRepository {

    val revealCircleQueue: Queue<RevealCircle> = LinkedList()
    val revealCircleListenerCaller: ListenerCaller<IRevealCircleListener, RevealCircle> = ListenerCaller()

    fun addLocation(location: Location) {
        val revealCircle = RevealCircle(location.latitude, location.longitude, 100.0)
        synchronized(this) {
//            if (locationQueue.size >= MAX_LOG_SIZE) {
//                locationQueue.poll()
//            }
            revealCircleQueue.add(revealCircle)
            revealCircleListenerCaller.callListeners(revealCircle)
        }
    }

    fun getLocations() : List<RevealCircle>{
        return revealCircleQueue.toList()
    }
}
