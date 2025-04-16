package com.justin13888.thumbhash

import com.justin13888.thumbhash.processors.ThumbHashProcessor
import kotlin.math.roundToInt

/**
 * ThumbHash is a very compact representation of a placeholder for an image.
 */
class ThumbHash {
    companion object {
        const val MAX_SIZE = ThumbHashProcessor.MAX_SIZE
        fun rgbaToThumbHash(w: Int, h: Int, rgba: ByteArray): ByteArray
            = ThumbHashProcessor.getInstance().rgbaToThumbHash(w, h, rgba)
        fun thumbHashToRGBA(hash: ByteArray): ThumbHash.Image
            = ThumbHashProcessor.getInstance().thumbHashToRGBA(hash)
        fun thumbHashToAverageRGBA(hash: ByteArray): ThumbHash.RGBA
            = ThumbHashProcessor.getInstance().thumbHashToAverageRGBA(hash)
        fun thumbHashToApproximateAspectRatio(hash: ByteArray): Float
            = ThumbHashProcessor.getInstance().thumbHashToApproximateAspectRatio(hash)
    }

    /**
     * Represents an image with width, height, and RGBA pixel data.
     */
    data class Image(
        val width: Int,
        val height: Int,
        val rgba: ByteArray
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Image) return false

            if (width != other.width) return false
            if (height != other.height) return false
            if (!rgba.contentEquals(other.rgba)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + rgba.contentHashCode()
            return result
        }
    }

    /**
     * Represents RGBA color values, each ranging from 0 to 1.
     */
    data class RGBA(
        val r: Float,
        val g: Float,
        val b: Float,
        val a: Float
    )
}