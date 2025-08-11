package com.dudoji.android.network.api.dto

interface BaseDto<T> {
    fun toDomain(): T
}