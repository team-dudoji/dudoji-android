package com.dudoji.android.model.mapsection

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@RunWith(AndroidJUnit4::class)
class Base64DecoderTester {

    val base64String = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAEBAQEBAQEBAQEBAQEBAQAAAAAAwMBggMFh0GGD9I9wqX5bxP6vxZy1XgUb9Fu6Lt5ONz7L3bLR2fuWrM/TtZrD4gt7yvlA5htV76RE9lJk9OUlIKSt0B5gD2jBrzFg5l56FjsAF2Hw5A3f15MmnOlgkIn2k47R5crzK8wB4gWhmXYuEmVmD9D9wAAAABJRU5ErkJggg"

    @Test
    @OptIn(ExperimentalEncodingApi::class)
    fun decodeBase64String() {
        val paddedBitmap = base64String.padEnd((base64String.length + 3) / 4 * 4, '=')
        assertTrue("Base64 string length must be a multiple of 4", paddedBitmap.length%4 == 0)

        val decodedBytes = Base64.decode(paddedBitmap)

        println("Decoded size: ${decodedBytes.size}")
    }
}