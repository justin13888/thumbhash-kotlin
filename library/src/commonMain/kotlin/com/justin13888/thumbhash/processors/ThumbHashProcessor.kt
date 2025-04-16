package com.justin13888.thumbhash.processors

import com.justin13888.thumbhash.ThumbHash.Image
import com.justin13888.thumbhash.ThumbHash.RGBA

abstract class ThumbHashProcessor {
    /**
     * Encodes an RGBA image to a ThumbHash. RGB should not be premultiplied by A.
     *
     * @param w    The width of the input image. Must be ≤[MAX_SIZE] for performance reasons.
     * @param h    The height of the input image. Must be ≤[MAX_SIZE]px for performance reasons.
     * @param rgba The pixels in the input image, row-by-row. Must contain exactly w*h*4 elements
     *             (4 bytes per pixel for R, G, B, and A channels).
     * @return The ThumbHash as a byte array.
     * @throws IllegalArgumentException If w or h exceeds [MAX_SIZE]px, or if rgba array length does not equal w*h*4.
     */
    abstract fun rgbaToThumbHash(
        w: Int,
        h: Int,
        rgba: ByteArray,
    ): ByteArray

    /**
     * Decodes a ThumbHash to an RGBA image. RGB is not be premultiplied by A.
     *
     * @param hash The bytes of the ThumbHash.
     * @return The width, height, and pixels of the rendered placeholder image.
     */
    abstract fun thumbHashToRGBA(hash: ByteArray): Image

    /**
     * Extracts the average color from a ThumbHash. RGB is not be premultiplied by A.
     *
     * @param hash The bytes of the ThumbHash.
     * @return The RGBA values for the average color. Each value ranges from 0 to 1.
     */
    abstract fun thumbHashToAverageRGBA(hash: ByteArray): RGBA

    /**
     * Extracts the approximate aspect ratio of the original image.
     *
     * @param hash The bytes of the ThumbHash.
     * @return The approximate aspect ratio (i.e. width / height).
     */
    abstract fun thumbHashToApproximateAspectRatio(hash: ByteArray): Float

    companion object {
        // TODO: Consider if we should really have an artificial limit even though the original implementation has it
        const val MAX_SIZE = 100

        private var forcedEngine: ThumbHashProcessor? = null
        private val standardEngine by lazy { StandardThumbHashProcessor() }
        private val simdEngine by lazy {
            if (simdAvailable()) SIMDThumbHashProcessor() else standardEngine
        }

        fun getInstance(): ThumbHashProcessor {
            return forcedEngine ?: if (simdAvailable()) simdEngine else standardEngine
        }

        /**
         * Force the use of SIMD engine if available.
         * @return true if SIMD engine is available and was forced, false otherwise.
         */
        fun forceSimd(): Boolean {
            return if (simdAvailable()) {
                forcedEngine = simdEngine
                true
            } else {
                false
            }
        }

        /**
         * Force the use of standard engine.
         */
        fun forceStandard() {
            forcedEngine = standardEngine
        }

        // TODO: Complete this so it could be statically compiled
        private fun simdAvailable(): Boolean {
            // Platform detection logic
            return false // Default implementation
        }
    }
}
