package com.justin13888.thumbhash

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

val random = Random(42) // Fixed seed for reproducibility

fun ByteArray.toHexString(): String = "0x" + this.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

/**
 * Creates an RGBA image with a solid color
 */
fun createSolidColorImage(
    width: Int,
    height: Int,
    r: Int,
    g: Int,
    b: Int,
    a: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    for (i in 0 until width * height) {
        val j = i * 4
        rgba[j] = r.toByte()
        rgba[j + 1] = g.toByte()
        rgba[j + 2] = b.toByte()
        rgba[j + 3] = a.toByte()
    }
    return rgba
}

/**
 * Creates a gradient image
 * @param width The width of the image
 * @param height The height of the image
 * @param horizontal If true, creates a horizontal gradient; otherwise, creates a vertical gradient
 */
fun createGradientImage(
    width: Int,
    height: Int,
    horizontal: Boolean = true,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val factor =
                if (horizontal) {
                    x.toFloat() / (width - 1)
                } else {
                    y.toFloat() / (height - 1)
                }

            rgba[i] = (factor * 255).toInt().toByte()
            rgba[i + 1] = ((1 - factor) * 255).toInt().toByte()
            rgba[i + 2] = (128).toByte()
            rgba[i + 3] = (255).toByte()
        }
    }
    return rgba
}

/**
 * Creates a checkerboard pattern
 * @param width The width of the image
 * @param height The height of the image
 * @param cellSize The size of each cell in the checkerboard
 * @param alphaPattern If true, uses an alpha pattern instead of a solid color
 */
fun createCheckerboardImage(
    width: Int,
    height: Int,
    cellSize: Int = 4,
    alphaPattern: Boolean = false,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val isWhiteCell = ((x / cellSize) + (y / cellSize)) % 2 == 0

            if (isWhiteCell) {
                rgba[i] = (255).toByte()
                rgba[i + 1] = (255).toByte()
                rgba[i + 2] = (255).toByte()
                rgba[i + 3] = if (alphaPattern) (128).toByte() else (255).toByte()
            } else {
                rgba[i] = (0).toByte()
                rgba[i + 1] = (0).toByte()
                rgba[i + 2] = (0).toByte()
                rgba[i + 3] = if (alphaPattern) (255).toByte() else (255).toByte()
            }
        }
    }
    return rgba
}

/**
 * Helper function to create a radial gradient
 */
fun createRadialGradientImage(
    width: Int,
    height: Int,
    withAlpha: Boolean = false,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    val centerX = width / 2.0f
    val centerY = height / 2.0f
    val maxDistance = kotlin.math.sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()

    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val distance = kotlin.math.sqrt(((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)).toDouble()).toFloat()
            val factor = distance / maxDistance

            rgba[i] = ((1 - factor) * 255).toInt().toByte()
            rgba[i + 1] = (0).toByte()
            rgba[i + 2] = (factor * 255).toInt().toByte()
            rgba[i + 3] = if (withAlpha) ((1 - factor) * 255).toInt().toByte() else (255).toByte()
        }
    }
    return rgba
}

/**
 * Creates a random image
 */
fun createRandomImage(
    width: Int,
    height: Int,
    randomAlpha: Boolean,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    random.nextBytes(rgba)

    if (!randomAlpha) {
        for (i in 0 until width * height) {
            rgba[i * 4 + 3] = 255.toByte() // Set alpha to 255
        }
    }

    return rgba
}

// TODO: Check if kotest suffices comparing bytearray vv

/**
 * Compares two RGBA images with tolerance
 */
fun compareRgbaImages(
    original: ByteArray,
    decoded: ByteArray,
    tolerance: Int,
): Boolean {
    if (original.size != decoded.size) return false

    for (i in original.indices) {
        val diff = abs((original[i].toInt() and 0xFF) - (decoded[i].toInt() and 0xFF))
        if (diff > tolerance) return false
    }

    return true
}

/**
 * Checks if [ThumbHash.Image] has consistent dimensions
 * @param image The image to check
 * @return True if the image has consistent dimensions, false otherwise
 */
fun ThumbHash.Image.isConsistent(): Boolean = ((this.width * this.height * 4) == this.rgba.size)

/**
 * Compare two hash byte arrays and return a similarity score (0-1).
 * 1.0 means identical, 0.0 means completely different.
 */
fun compareHashes(
    hash1: ByteArray,
    hash2: ByteArray,
): Float {
    if (hash1.size != hash2.size) return 0.0f

    var matches = 0
    val total = hash1.size * 8 // Count bits, not bytes

    for (i in hash1.indices) {
        val byte1 = hash1[i].toInt() and 0xFF
        val byte2 = hash2[i].toInt() and 0xFF

        // Count matching bits
        val xor = byte1 xor byte2

        // Check all 8 bits in each byte
        for (j in 0 until 8) {
            if (((xor shr j) and 1) == 0) {
                matches++
            }
        }
    }

    return matches.toFloat() / total
}

/**
 * Extracts and validates basic hash structure properties
 */
fun extractHashProperties(hash: ByteArray): Map<String, Any> {
    if (hash.size < 5) {
        throw IllegalArgumentException("Hash too short to be valid")
    }

    val header24 =
        (hash[0].toInt() and 0xFF) or
            ((hash[1].toInt() and 0xFF) shl 8) or
            ((hash[2].toInt() and 0xFF) shl 16)
    val header16 = (hash[3].toInt() and 0xFF) or ((hash[4].toInt() and 0xFF) shl 8)

    val lDc = (header24 and 63) / 63.0f
    val pDc = ((header24 shr 6) and 63) / 31.5f - 1.0f
    val qDc = ((header24 shr 12) and 63) / 31.5f - 1.0f
    val lScale = ((header24 shr 18) and 31) / 31.0f
    val hasAlpha = (header24 shr 23) != 0
    val pScale = ((header16 shr 3) and 63) / 63.0f
    val qScale = ((header16 shr 9) and 63) / 63.0f
    val isLandscape = (header16 shr 15) != 0

    val result =
        mutableMapOf<String, Any>(
            "hasAlpha" to hasAlpha,
            "isLandscape" to isLandscape,
            "lDc" to lDc,
            "pDc" to pDc,
            "qDc" to qDc,
            "lScale" to lScale,
            "pScale" to pScale,
            "qScale" to qScale,
        )

    if (hasAlpha && hash.size > 5) {
        val aDc = (hash[5].toInt() and 15) / 15.0f
        val aScale = ((hash[5].toInt() shr 4) and 15) / 15.0f
        result["aDc"] = aDc
        result["aScale"] = aScale
    }

    return result
}

/**
 * Calculates average color in a specific region of an image
 */
fun averageRegionColor(
    image: ThumbHash.Image,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
): Triple<Float, Float, Float> {
    var totalR = 0.0f
    var totalG = 0.0f
    var totalB = 0.0f
    var count = 0

    val right = minOf(x + width, image.width)
    val bottom = minOf(y + height, image.height)

    for (py in y until bottom) {
        for (px in x until right) {
            val i = (py * image.width + px) * 4
            totalR += (image.rgba[i].toInt() and 0xFF) / 255.0f
            totalG += (image.rgba[i + 1].toInt() and 0xFF) / 255.0f
            totalB += (image.rgba[i + 2].toInt() and 0xFF) / 255.0f
            count++
        }
    }

    return if (count > 0) {
        Triple(totalR / count, totalG / count, totalB / count)
    } else {
        Triple(0.0f, 0.0f, 0.0f)
    }
}

/**
 * Helper function to create a flat color image
 */
fun createFlatColorImage(
    width: Int,
    height: Int,
    r: Int,
    g: Int,
    b: Int,
    a: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    for (i in 0 until width * height) {
        val j = i * 4
        rgba[j] = r.toByte()
        rgba[j + 1] = g.toByte()
        rgba[j + 2] = b.toByte()
        rgba[j + 3] = a.toByte()
    }
    return rgba
}

/**
 * Helper function to calculate mean squared error between two images
 */
fun calculateMSE(
    img1: ByteArray,
    img2: ByteArray,
): Double {
    if (img1.size != img2.size) {
        throw IllegalArgumentException("Images must be the same size")
    }

    var sumSquaredError = 0.0
    for (i in img1.indices) {
        val diff = (img1[i].toInt() and 0xFF) - (img2[i].toInt() and 0xFF)
        sumSquaredError += diff * diff
    }

    return sumSquaredError / img1.size
}

/**
 * Helper function to create a checkerboard pattern
 */
fun createCheckerboard(
    width: Int,
    height: Int,
    cellSize: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val isWhite = ((x / cellSize) + (y / cellSize)) % 2 == 0

            if (isWhite) {
                rgba[i] = -1 // R (255)
                rgba[i + 1] = -1 // G (255)
                rgba[i + 2] = -1 // B (255)
            } else {
                rgba[i] = 0 // R (0)
                rgba[i + 1] = 0 // G (0)
                rgba[i + 2] = 0 // B (0)
            }
            rgba[i + 3] = -1 // A (255)
        }
    }

    return rgba
}

/**
 * Helper function to create a gradient pattern
 */
fun createGradient(
    width: Int,
    height: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)

    for (y in 0 until height) {
        val g = (y * 255 / height).toByte()
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val r = (x * 255 / width).toByte()
            val b = ((x + y) * 127 / (width + height)).toByte()

            rgba[i] = r // R
            rgba[i + 1] = g // G
            rgba[i + 2] = b // B
            rgba[i + 3] = -1 // A (255)
        }
    }

    return rgba
}

/**
 * Helper function to create a circular pattern
 */
fun createCircularPattern(
    width: Int,
    height: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)
    val centerX = width / 2.0
    val centerY = height / 2.0
    val maxRadius = sqrt((centerX * centerX + centerY * centerY).toDouble())

    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val dx = x - centerX
            val dy = y - centerY
            val distance = sqrt(dx * dx + dy * dy)
            val normalizedDistance = distance / maxRadius

            val angle = kotlin.math.atan2(dy, dx) + kotlin.math.PI // 0 to 2π
            val normalizedAngle = angle / (2 * kotlin.math.PI)

            val r = (normalizedDistance * 255).toInt().toByte()
            val g = (normalizedAngle * 255).toInt().toByte()
            val b = ((1.0 - normalizedDistance) * 255).toInt().toByte()

            rgba[i] = r // R
            rgba[i + 1] = g // G
            rgba[i + 2] = b // B
            rgba[i + 3] = -1 // A (255)
        }
    }

    return rgba
}

/**
 * Helper function to create a pattern with transparency
 */
fun createTransparencyPattern(
    width: Int,
    height: Int,
): ByteArray {
    val rgba = ByteArray(width * height * 4)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = (y * width + x) * 4
            val normalizedX = x.toFloat() / width
            val normalizedY = y.toFloat() / height

            rgba[i] = (normalizedX * 255).toInt().toByte() // R
            rgba[i + 1] = (normalizedY * 255).toInt().toByte() // G
            rgba[i + 2] = ((1 - normalizedX) * 255).toInt().toByte() // B

            // Create a circular transparency pattern
            val dx = x - width / 2.0
            val dy = y - height / 2.0
            val distance = sqrt(dx * dx + dy * dy)
            val alpha = (255 * (1.0 - kotlin.math.min(1.0, distance / (width / 2.0)))).toInt().toByte()
            rgba[i + 3] = alpha // Alpha
        }
    }

    return rgba
}
