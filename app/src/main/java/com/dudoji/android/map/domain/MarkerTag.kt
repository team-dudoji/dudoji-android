package com.dudoji.android.map.domain

enum class MarkerType {
    DEFAULT,
    PLAYER,
    PIN,
}

data class MarkerTag<T> (val tag: MarkerType, val data: T)

