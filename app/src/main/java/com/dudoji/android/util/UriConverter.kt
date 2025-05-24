package com.dudoji.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

object UriConverter {
    fun uriToMultipartBodyPart(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return MultipartBody.Part.createFormData(partName, null, RequestBody.create(null, byteArrayOf()))

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val ratio = minOf(
            512f / originalBitmap.width,
            512f / originalBitmap.height
        )

        val newWidth = (originalBitmap.width * ratio).toInt()
        val newHeight = (originalBitmap.height * ratio).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

        val requestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), tempFile)

        return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    }
}