package com.dudoji.android.util.listener

interface IListener<U> {
    fun onEvent(event: U)
}