package com.justin13888.thumbhash

//import io.kotest.core.spec.style.StringSpec
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.shouldNotBe
//import io.kotest.matchers.floats.shouldBeLessThan
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.matchers.booleans.shouldBeTrue
//import io.kotest.matchers.booleans.shouldBeFalse
//import io.kotest.matchers.floats.shouldBeGreaterThan
//import io.kotest.matchers.floats.shouldBeLessThanOrEqual
//import io.kotest.matchers.floats.shouldBeGreaterThanOrEqual
//import io.kotest.matchers.nulls.shouldNotBeNull
//import kotlin.math.abs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.floats.plusOrMinus

class ThumbHashTest : StringSpec({
    "should encode and decode solid color image" {
        val width = 32
        val height = 32
        val rgba = createSolidColorImage(width, height, 255, 0, 0, 255) // Solid red

        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
        hash.isNotEmpty().shouldBeTrue()

        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
        decodedImage.isConsistent().shouldBeTrue()

        // Verify the average color is red
        val avgColor = ThumbHash.thumbHashToAverageRGBA(hash)
        withClue("Average color should be red; got: $avgColor") {
            avgColor.r.shouldBe(1.0f plusOrMinus 0.01f)
            avgColor.g.shouldBe(0.0f plusOrMinus 0.01f)
            avgColor.b.shouldBe(0.0f plusOrMinus 0.01f)
            avgColor.a.shouldBe(1.0f plusOrMinus 0.01f)
        }
    }

    "should handle various image dimensions" {
        val testSizes = listOf(
            Pair(16, 16),   // Small square
            Pair(64, 32),   // Landscape
            Pair(32, 64),   // Portrait
            Pair(100, 100)  // Maximum allowed size
        )

        for ((width, height) in testSizes) {
            val rgba = createSolidColorImage(width, height, 0, 255, 0, 255) // Green

            val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)

            // Check hash metadata
            val props = extractHashProperties(hash)
            if (width > height) {
                props["isLandscape"] shouldBe true
            } else if (width < height) {
                props["isLandscape"] shouldBe false
            }

            // Check aspect ratio
            val aspectRatio = ThumbHash.thumbHashToApproximateAspectRatio(hash)
            val originalRatio = width.toFloat() / height.toFloat()
            aspectRatio.shouldBeWithinPercentageOf(originalRatio, 25.0) // Allow tolerance due to compression
        }
    }

    "should reject images exceeding MAX_SIZE" {
        val width = MAX_SIZE + 1
        val height = 50
        val rgba = createSolidColorImage(width, height, 0, 0, 255, 255) // Blue

        shouldThrow<IllegalArgumentException> {
            ThumbHash.rgbaToThumbHash(width, height, rgba)
        }
    }

    "should reject invalid RGBA array sizes" {
        val width = 32
        val height = 32
        val rgba = ByteArray(width * height * 3) // 3 bytes per pixel instead of 4

        shouldThrow<IllegalArgumentException> {
            ThumbHash.rgbaToThumbHash(width, height, rgba)
        }
    }

    "should properly handle alpha channel" {
        val width = 64
        val height = 64

        // Test fully opaque image
        val opaqueImage = createSolidColorImage(width, height, 255, 0, 0, 255)
        val opaqueHash = ThumbHash.rgbaToThumbHash(width, height, opaqueImage)
        val opaqueProps = extractHashProperties(opaqueHash)
        opaqueProps["hasAlpha"] shouldBe false

        // Test transparent image
        val transparentImage = createSolidColorImage(width, height, 255, 0, 0, 128)
        val transparentHash = ThumbHash.rgbaToThumbHash(width, height, transparentImage)
        val transparentProps = extractHashProperties(transparentHash)
        transparentProps["hasAlpha"] shouldBe true
        transparentProps shouldContainKey "aDc"
        transparentProps shouldContainKey "aScale"

        // Verify alpha value in average color
        val avgColor = ThumbHash.thumbHashToAverageRGBA(transparentHash)
        avgColor.a.shouldBeWithinPercentageOf(128f / 255f, 20.0)
    }

    "should handle various image patterns" {
        val width = 64
        val height = 64

        val patterns = listOf(
            createGradientImage(width, height, horizontal = true),
            createGradientImage(width, height, horizontal = false),
            createCheckerboardImage(width, height, cellSize = 8),
            createRadialGradientImage(width, height)
        )

        for (rgba in patterns) {
            val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
            hash.isNotEmpty().shouldBeTrue()

            val decodedImage = ThumbHash.thumbHashToRGBA(hash)
            decodedImage.isConsistent().shouldBeTrue()
        }
    }

//    "should preserve general image content in round-trip conversion" {
//        val width = 64
//        val height = 48
//        val rgba = createGradientImage(width, height, true) // Horizontal gradient
//
//        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
//
//        // We don't expect pixel-perfect matching but general characteristics should be preserved
//        // Test left side is more red, right side more green (for horizontal gradient)
//        val leftColor = averageRegionColor(decodedImage, 0, 0, 10, decodedImage.height)
//        val rightColor = averageRegionColor(decodedImage, decodedImage.width - 10, 0, 10, decodedImage.height)
//
//        (leftColor.first > leftColor.second).shouldBeTrue() // Left: R > G
//        (rightColor.second > rightColor.first).shouldBeTrue() // Right: G > R
//    }

//    "should accurately extract average color" {
//        val testColors = listOf(
//            Triple(255, 0, 0),     // Red
//            Triple(0, 255, 0),     // Green
//            Triple(0, 0, 255),     // Blue
//            Triple(255, 255, 0),   // Yellow
//            Triple(0, 255, 255),   // Cyan
//            Triple(255, 0, 255),   // Magenta
//            Triple(255, 255, 255), // White
//            Triple(0, 0, 0)        // Black
//        )
//
//        for ((r, g, b) in testColors) {
//            val width = 32
//            val height = 32
//            val rgba = createSolidColorImage(width, height, r, g, b, 255)
//
//            val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//            val avgColor = ThumbHash.thumbHashToAverageRGBA(hash)
//
//            // Convert byte values (0-255) to float (0-1) for comparison
//            val expectedR = r / 255.0f
//            val expectedG = g / 255.0f
//            val expectedB = b / 255.0f
//
//            avgColor.r.shouldBeWithinPercentageOf(expectedR, 15.0)
//            avgColor.g.shouldBeWithinPercentageOf(expectedG, 15.0)
//            avgColor.b.shouldBeWithinPercentageOf(expectedB, 15.0)
//        }
//    }

    "should generate consistent hash for identical images" {
        val width = 64
        val height = 48
        val rgba1 = createSolidColorImage(width, height, 100, 150, 200, 255)
        val rgba2 = createSolidColorImage(width, height, 100, 150, 200, 255)

        val hash1 = ThumbHash.rgbaToThumbHash(width, height, rgba1)
        val hash2 = ThumbHash.rgbaToThumbHash(width, height, rgba2)

        // Hashes for identical images should be identical
        hash1.size shouldBe hash2.size
        for (i in hash1.indices) {
            hash1[i] shouldBe hash2[i]
        }

        // Test similarity score is 1.0 (identical)
        withClue("Identical images should have ~1.0 similarity score: ${hash1.toHexString()} vs ${hash2.toHexString()}") {
            compareHashes(hash1, hash2).shouldBeWithinPercentageOf(1.0f, 0.1)
        } // TODO: Why do it like this?
    }

    "should handle tiny images" {
        val width = 2
        val height = 2
        val rgba = createSolidColorImage(width, height, 128, 128, 128, 255) // Gray

        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
        hash.isNotEmpty().shouldBeTrue()

        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
        decodedImage.isConsistent().shouldBeTrue()
    }

    "Image objects should follow equals contract" {
        val width = 32
        val height = 32
        val rgba = createSolidColorImage(width, height, 255, 0, 0, 255) // Red

        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
        val image1 = ThumbHash.thumbHashToRGBA(hash)
        val image2 = ThumbHash.thumbHashToRGBA(hash)

        // Same hash should produce equal images
        (image1 == image2).shouldBeTrue()
        (image1.hashCode() == image2.hashCode()).shouldBeTrue()

        // Different images should not be equal
        val differentRgba = createSolidColorImage(width, height, 0, 255, 0, 255) // Green
        val differentHash = ThumbHash.rgbaToThumbHash(width, height, differentRgba)
        val differentImage = ThumbHash.thumbHashToRGBA(differentHash)

        (image1 == differentImage).shouldBeFalse()
    }

    "hash size should vary with image complexity and alpha" {
        val width = 64
        val height = 64

        // Simple color, no alpha
        val solidNoAlpha = createSolidColorImage(width, height, 100, 150, 200, 255)
        val solidNoAlphaHash = ThumbHash.rgbaToThumbHash(width, height, solidNoAlpha)

        // Simple color with alpha
        val solidWithAlpha = createSolidColorImage(width, height, 100, 150, 200, 128)
        val solidWithAlphaHash = ThumbHash.rgbaToThumbHash(width, height, solidWithAlpha)

        // Alpha images use one more byte for alpha channel info
        val noAlphaProps = extractHashProperties(solidNoAlphaHash)
        val withAlphaProps = extractHashProperties(solidWithAlphaHash)

        noAlphaProps["hasAlpha"] shouldBe false
        withAlphaProps["hasAlpha"] shouldBe true

        // Hash with alpha should be larger or equal in size (due to alpha channel encoding)
        (solidWithAlphaHash.size >= solidNoAlphaHash.size).shouldBeTrue()
    }

    "should verify RGBA comparison function works properly" {
        val width = 32
        val height = 32
        val original = createSolidColorImage(width, height, 100, 150, 200, 255)

        // Create a slightly different image
        val slightly = createSolidColorImage(width, height, 105, 155, 205, 255)

        // Create a very different image
        val different = createSolidColorImage(width, height, 200, 50, 100, 255)

        // Test with different tolerances
        compareRgbaImages(original, original, 0).shouldBeTrue() // Identical, zero tolerance
        compareRgbaImages(original, slightly, 5).shouldBeTrue() // Slight difference within tolerance
        compareRgbaImages(original, slightly, 4).shouldBeFalse() // Slight difference beyond tolerance
        compareRgbaImages(original, different, 10).shouldBeFalse() // Large difference beyond tolerance
    }
})
