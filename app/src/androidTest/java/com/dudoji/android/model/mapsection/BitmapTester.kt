package com.dudoji.android.model.mapsection

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapTester {
    lateinit var bitmap : Bitmap

    @Test
    fun testBitmapCreate () {
        bitmap = Bitmap("/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAEBAQEBAQEBAQEBAQEBAQAAAAAAwMBggMFh0GGD9I9wqX5bxP6vxZy1XgUb9Fu6Lt5ONz7L3bLR2fuWrM/TtZrD4gt7yvlA5htV76RE9lJk9OUlIKSt0B5gD2jBrzFg5l56FjsAF2Hw5A3f15MmnOlgkIn2k47R5crzK8wB4gWhmXYuEmVmD9D9wAAAABJRU5ErkJggg==")

        assertEquals(128 * 128, bitmap.size)
        print("bitmap successfully created")
    }

    @Test
    fun testBitmapGetSet () {
        bitmap[0][0] = true
        assertEquals(true, bitmap[0][0])
        bitmap[0][0] = false
        assertEquals(false, bitmap[0][0])
        print("bitmap successfully work with get and set")
    }
}