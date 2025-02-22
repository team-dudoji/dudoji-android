package com.dudoji.android.util.listener

class ListenerCaller<T, U> where T: IListener<U> {
    private val listeners = mutableListOf<T>()
    fun addListener(listener: T) {
        listeners.add(listener)
    }
    fun removeListener(listener: T) {
        listeners.remove(listener)
    }
    fun callListeners(data: U) {
        listeners.forEach { it.onEvent(data) }
    }
}