package com.dudoji.android.model.mapsection

import com.dudoji.android.util.base64.Base64Decoder
import kotlin.io.encoding.ExperimentalEncodingApi

const val MAP_SECTION_SIZE = 256
const val BASIC_ZOOM_LEVEL = 15

class Bitmap {
    val bitMap : ByteArray
    val size : Int
        get() = bitMap.size * 8

    @OptIn(ExperimentalEncodingApi::class)
    constructor(bitmap: String){
        bitMap = Base64Decoder().decode(bitmap)
    }

    operator fun get(index: Int): SubBitArray {
        return SubBitArray(
            bitMap,
            index * (MAP_SECTION_SIZE / 8),
            (index + 1) * (MAP_SECTION_SIZE / 8))
    }

    // Returns the percentage of filled bits in the bitmap
    fun getFilledRate(): Float {
        val totalBits = size
        val filledBits = bitMap.sumOf { byte -> Integer.bitCount(byte.toInt() and 0xFF) }

        return (filledBits.toFloat() / totalBits) * 100f
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