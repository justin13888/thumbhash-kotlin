package com.justin13888.thumbhash

import kotlin.math.abs
import kotlin.random.Random

/**
 * Helper function to generate random RGBA image data for testing
 */
fun generateRandomImage(
    width: Int,
    height: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    Random.nextBytes(rgba)
    return rgba
}

/**
 * Helper function to compare two RGBA objects for near-equality (with floating point comparison)
 */
fun areRgbaColorsEqual(
    a: ThumbHash.RGBA,
    b: com.madebyevan.thumbhash.ThumbHash.RGBA,
): Boolean {
    val epsilon = 0.0001f
    return (
        abs(a.r - b.r) < epsilon &&
            abs(a.g - b.g) < epsilon &&
            abs(a.b - b.b) < epsilon &&
            abs(a.a - b.a) < epsilon
    )
}

/**
 * Helper function to compare two image objects for equality
 */
fun areImagesEqual(
    a: ThumbHash.Image,
    b: com.madebyevan.thumbhash.ThumbHash.Image,
): Boolean {
    if (a.width != b.width || a.height != b.height || a.rgba.size != b.rgba.size) {
        return false
    }

    // Verify each byte in the RGBA arrays
    for (i in a.rgba.indices) {
        if (a.rgba[i] != b.rgba[i]) {
            return false
        }
    }

    return true
}
