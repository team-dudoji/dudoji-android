package com.dudoji.android.util.base64

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Base64Decoder {
    companion object {
        // Decodes a base64 encoded string by Chunk
        @OptIn(ExperimentalEncodingApi::class)
        fun decode(encoded: String): ByteArray {
            val chunkSize = 256 // 256 bytes is the maximum size of a chunk that can be decoded
            val output = mutableListOf<Byte>()

            var start = 0
            while (start < encoded.length) {
                val end = minOf(start + chunkSize, encoded.length)
                val chunk = encoded.substring(start, end)
                val paddedChunk = chunk.padEnd((chunk.length + 3) / 4 * 4, '=')
                val decodedChunk = Base64.decode(paddedChunk)
                output.addAll(decodedChunk.toList())
                start = end
            }

            return output.toByteArray()
        }
    }
}
