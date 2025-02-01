package com.dudoji.android.model.mapsection

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val MAP_SECTION_SIZE = 128

class Bitmap {
    val bitMap : ByteArray

    @OptIn(ExperimentalEncodingApi::class)
    constructor(bitmap: String){
        bitMap = Base64.decode(bitmap)
    }

    operator fun get(index: Int): SubBitArray {
        return SubBitArray(
            bitMap,
            index * (MAP_SECTION_SIZE / 8),
            (index + 1) * (MAP_SECTION_SIZE / 8))
    }

    class SubBitArray(val byteArray: ByteArray, val start: Int, val end: Int) {
        val size: Int
            get() = (end - start) * 8

        operator fun get(index: Int): Boolean {
            require(index in 0 until size) { "Index out of bounds: $index" }
            val byteIndex = index / 8 + start
            val bitIndex = index % 8
            return (byteArray[byteIndex].toInt() shr (7 - bitIndex) and 1) == 1
        }

        operator fun set(index: Int, value: Boolean) {
            require(index in 0 until size) { "Index out of bounds: $index" }
            val byteIndex = index / 8 + start
            val bitIndex = index % 8
            val mask = (1 shl (7 - bitIndex))

            if (value) {
                byteArray[byteIndex] = (byteArray[byteIndex].toInt() or mask).toByte()
            } else {
                byteArray[byteIndex] = (byteArray[byteIndex].toInt() and mask.inv()).toByte()
            }
        }
    }
}