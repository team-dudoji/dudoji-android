package com.dudoji.android.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

object UriConverter {
    fun uriToMultipartBodyPart(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return MultipartBody.Part.createFormData(partName, null, RequestBody.create(null, byteArrayOf()))

        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        val mimeType = context.contentResolver.getType(uri) ?: "image/*"

        val requestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), tempFile)

        return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    }
}