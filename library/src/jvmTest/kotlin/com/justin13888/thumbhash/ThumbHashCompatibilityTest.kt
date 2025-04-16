package com.justin13888.thumbhash

import io.kotest.core.spec.style.StringSpec

// TODO: Make any necessary changes here

/**
 * Tests that ensure the Kotlin implementation of ThumbHash matches the behavior
 * of the original Java implementation from https://github.com/evanw/thumbhash
 */
class ThumbHashCompatibilityTest : StringSpec({
//    "rgbaToThumbHash should produce identical output to Java implementation" {
//        // Test with various sizes of images
//        val testSizes = listOf(
//            Pair(10, 10),
//            Pair(32, 24),
//            Pair(40, 30),
//            Pair(100, 75),
//            Pair(25, 100),
//            Pair(1, 1)
//        )
//
//        for ((width, height) in testSizes) {
//            val rgba = generateRandomImage(width, height)
//
//            // Generate hash with both implementations
//            val kotlinHash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//            val javaHash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, rgba)
//
//            // The hashes should be identical
//            kotlinHash.contentEquals(javaHash) shouldBe true
//        }
//    }
//
//    "rgbaToThumbHash should throw exception for sizes larger than MAX_SIZE" {
//        val rgba = generateRandomImage(MAX_SIZE + 1, 50)
//        try {
//            ThumbHash.rgbaToThumbHash(MAX_SIZE + 1, 50, rgba)
//            throw AssertionError("Should have thrown IllegalArgumentException")
//        } catch (e: IllegalArgumentException) {
//            // Expected behavior
//        }
//    }
//
//    "thumbHashToRGBA should produce identical output to Java implementation" {
//        // Test with various sizes of images
//        val testSizes = listOf(
//            Pair(10, 10),
//            Pair(32, 24),
//            Pair(40, 30),
//            Pair(100, 75)
//        )
//
//        for ((width, height) in testSizes) {
//            val rgba = generateRandomImage(width, height)
//
//            // Generate hash with Java implementation
//            val hash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, rgba)
//
//            // Decode with both implementations
//            val kotlinImage = ThumbHash.thumbHashToRGBA(hash)
//            val javaImage = com.madebyevan.thumbhash.ThumbHash.thumbHashToRGBA(hash)
//
//            // The decoded images should be equivalent
//            areImagesEqual(kotlinImage, javaImage) shouldBe true
//        }
//    }
//
//    "thumbHashToAverageRGBA should produce identical output to Java implementation" {
//        // Test with various sizes of images
//        val testSizes = listOf(
//            Pair(10, 10),
//            Pair(32, 24),
//            Pair(40, 30),
//            Pair(100, 75),
//            Pair(25, 100)
//        )
//
//        for ((width, height) in testSizes) {
//            val rgba = generateRandomImage(width, height)
//
//            // Generate hash with Java implementation
//            val hash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, rgba)
//
//            // Get average RGBA with both implementations
//            val kotlinRgba = ThumbHash.thumbHashToAverageRGBA(hash)
//            val javaRgba = com.madebyevan.thumbhash.ThumbHash.thumbHashToAverageRGBA(hash)
//
//            // The average colors should be equivalent (within floating point precision)
//            areRgbaColorsEqual(kotlinRgba, javaRgba) shouldBe true
//        }
//    }
//
//    "thumbHashToApproximateAspectRatio should produce identical output to Java implementation" {
//        // Test with various sizes of images
//        val testSizes = listOf(
//            Pair(10, 10),  // 1:1
//            Pair(32, 24),  // 4:3
//            Pair(40, 30),  // 4:3
//            Pair(100, 75), // 4:3
//            Pair(25, 100), // 1:4
//            Pair(100, 25)  // 4:1
//        )
//
//        for ((width, height) in testSizes) {
//            val rgba = generateRandomImage(width, height)
//
//            // Generate hash with Java implementation
//            val hash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, rgba)
//
//            // Get aspect ratio with both implementations
//            val kotlinAspectRatio = ThumbHash.thumbHashToApproximateAspectRatio(hash)
//            val javaAspectRatio = com.madebyevan.thumbhash.ThumbHash.thumbHashToApproximateAspectRatio(hash)
//
//            // The aspect ratios should be identical (within floating point precision)
//            val epsilon = 0.0001f
//            kotlin.math.abs(kotlinAspectRatio - javaAspectRatio) < epsilon shouldBe true
//        }
//    }
//
//    "round-trip encoding and decoding should preserve image characteristics" {
//        // Test with various images
//        val testSizes = listOf(
//            Pair(32, 32),
//            Pair(64, 48),
//            Pair(100, 75),
//            Pair(25, 100)
//        )
//
//        for ((width, height) in testSizes) {
//            val originalRgba = generateRandomImage(width, height)
//
//            // Encode with Kotlin implementation
//            val hash = ThumbHash.rgbaToThumbHash(width, height, originalRgba)
//
//            // Decode with Kotlin implementation
//            val decodedImage = ThumbHash.thumbHashToRGBA(hash)
//
//            // Verify the aspect ratio is roughly preserved
//            // (Note: ThumbHash is a lossy format, so exact dimensions won't match)
//            val originalAspect = width.toFloat() / height
//            val decodedAspect = decodedImage.width.toFloat() / decodedImage.height
//
//            val aspectRatioError = kotlin.math.abs(originalAspect - decodedAspect) / originalAspect
//            aspectRatioError < 0.2f shouldBe true // Allow 20% error margin for aspect ratio
//        }
//    }
//
//    "Image data class equality and hashCode should work correctly" {
//        val rgba1 = ByteArray(12) { it.toByte() }
//        val rgba2 = ByteArray(12) { it.toByte() }
//        val rgba3 = ByteArray(12) { (it + 1).toByte() }
//
//        val image1 = ThumbHash.Image(3, 1, rgba1)
//        val image2 = ThumbHash.Image(3, 1, rgba2)
//        val image3 = ThumbHash.Image(3, 1, rgba3)
//        val image4 = ThumbHash.Image(1, 3, rgba1)
//
//        // Identical content should be equal
//        (image1 == image2) shouldBe true
//        image1.hashCode() shouldBe image2.hashCode()
//
//        // Different content shouldn't be equal
//        (image1 == image3) shouldBe false
//        (image1 == image4) shouldBe false
//    }
//
//    "RGBA data class should work correctly" {
//        val rgba1 = ThumbHash.RGBA(0.1f, 0.2f, 0.3f, 0.4f)
//        val rgba2 = ThumbHash.RGBA(0.1f, 0.2f, 0.3f, 0.4f)
//        val rgba3 = ThumbHash.RGBA(0.5f, 0.2f, 0.3f, 0.4f)
//
//        // Identical content should be equal
//        (rgba1 == rgba2) shouldBe true
//        rgba1.hashCode() shouldBe rgba2.hashCode()
//
//        // Different content shouldn't be equal
//        (rgba1 == rgba3) shouldBe false
//    }
//
//    "should handle completely transparent images correctly" {
//        val width = 32
//        val height = 24
//        val rgba = createFlatColorImage(width, height, 255, 0, 0, 0) // Red, but fully transparent
//
//        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
//        val avgColor = ThumbHash.thumbHashToAverageRGBA(hash)
//
//        // For completely transparent images, average alpha should be near-zero
//        avgColor.a < 0.1f shouldBe true
//    }
//
//    "should handle minimum size images (1x1)" {
//        val rgba = createFlatColorImage(1, 1, 255, 128, 0, 255) // Orange pixel
//
//        val hash = ThumbHash.rgbaToThumbHash(1, 1, rgba)
//        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
//        val avgColor = ThumbHash.thumbHashToAverageRGBA(hash)
//
//        // Verify average color matches input (normalized to 0-1)
//        avgColor.r > 0.9f shouldBe true // Should be close to 1.0 (255/255)
//        avgColor.g > 0.45f && avgColor.g < 0.55f shouldBe true // Should be close to 0.5 (128/255)
//        avgColor.b < 0.1f shouldBe true // Should be close to 0 (0/255)
//        avgColor.a > 0.9f shouldBe true // Should be close to 1.0 (255/255)
//    }
//
//    "should handle maximum size images (100x100)" {
//        val width = 100
//        val height = 100
//        val rgba = createFlatColorImage(width, height, 0, 0, 255, 255) // Blue
//
//        // This should not throw an exception
//        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
//
//        // Verify decoded image has approximately the right size (ThumbHash always decodes to ~32px)
//        decodedImage.width shouldBe 32
//        decodedImage.height shouldBe 32
//    }
//
//    "should reject image data with incorrect size" {
//        val width = 10
//        val height = 10
//        val rgbaTooSmall = ByteArray(width * height * 4 - 4) // Missing one pixel
//
//        try {
//            ThumbHash.rgbaToThumbHash(width, height, rgbaTooSmall)
//            throw AssertionError("Should have thrown IllegalArgumentException for too small data")
//        } catch (e: IllegalArgumentException) {
//            // Expected behavior
//        }
//
//        val rgbaTooLarge = ByteArray(width * height * 4 + 4) // One extra pixel
//
//        try {
//            ThumbHash.rgbaToThumbHash(width, height, rgbaTooLarge)
//            throw AssertionError("Should have thrown IllegalArgumentException for too large data")
//        } catch (e: IllegalArgumentException) {
//            // Expected behavior
//        }
//    }
//
//    "should handle grayscale images correctly" {
//        val width = 20
//        val height = 20
//        val rgba = ByteArray(width * height * 4)
//
//        // Create a grayscale gradient
//        for (y in 0 until height) {
//            val value = (y * 255 / height).toByte()
//            for (x in 0 until width) {
//                val i = (y * width + x) * 4
//                rgba[i] = value     // R
//                rgba[i + 1] = value // G
//                rgba[i + 2] = value // B
//                rgba[i + 3] = -1    // Alpha (255)
//            }
//        }
//
//        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//        val decodedImage = ThumbHash.thumbHashToRGBA(hash)
//        val avgColor = ThumbHash.thumbHashToAverageRGBA(hash)
//
//        // For grayscale, RGB values should be approximately equal
//        val epsilon = 0.1f
//        kotlin.math.abs(avgColor.r - avgColor.g) < epsilon shouldBe true
//        kotlin.math.abs(avgColor.r - avgColor.b) < epsilon shouldBe true
//    }
//
//    "should handle extreme aspect ratios" {
//        // Very wide image
//        run {
//            val width = 100
//            val height = 1
//            val rgba = createFlatColorImage(width, height, 255, 0, 0, 255)
//
//            val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//            val aspectRatio = ThumbHash.thumbHashToApproximateAspectRatio(hash)
//
//            // Should report a high aspect ratio
//            aspectRatio > 1.0f shouldBe true
//        }
//
//        // Very tall image
//        run {
//            val width = 1
//            val height = 100
//            val rgba = createFlatColorImage(width, height, 0, 255, 0, 255)
//
//            val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//            val aspectRatio = ThumbHash.thumbHashToApproximateAspectRatio(hash)
//
//            // Should report a low aspect ratio
//            aspectRatio < 1.0f shouldBe true
//        }
//    }
//
//    "should handle partial transparency correctly" {
//        val width = 20
//        val height = 20
//        val rgba = ByteArray(width * height * 4)
//
//        // Create a gradient of transparency
//        for (y in 0 until height) {
//            val alpha = (y * 255 / height).toByte()
//            for (x in 0 until width) {
//                val i = (y * width + x) * 4
//                rgba[i] = -1       // R (255)
//                rgba[i + 1] = 0    // G
//                rgba[i + 2] = -1   // B (255)
//                rgba[i + 3] = alpha // Varying alpha
//            }
//        }
//
//        val hash = ThumbHash.rgbaToThumbHash(width, height, rgba)
//        val avgColor = ThumbHash.thumbHashToAverageRGBA(hash)
//
//        // Should have partial transparency
//        avgColor.a > 0.1f && avgColor.a < 0.9f shouldBe true
//
//        // Should accurately represent the magenta color
//        avgColor.r > 0.7f shouldBe true
//        avgColor.g < 0.3f shouldBe true
//        avgColor.b > 0.7f shouldBe true
//    }
//
//    "should handle hash byte array consistency" {
//        val width = 50
//        val height = 40
//        val rgba = ByteArray(width * height * 4)
//        Random.nextBytes(rgba)
//
//        // Generate two hashes from the same image data
//        val hash1 = ThumbHash.rgbaToThumbHash(width, height, rgba)
//        val hash2 = ThumbHash.rgbaToThumbHash(width, height, rgba)
//
//        // Hashes of the same image should be identical
//        hash1.contentEquals(hash2) shouldBe true
//    }
//
//    "should handle small but with different aspect ratios" {
//        // Nearly square
//        run {
//            val rgba1 = createFlatColorImage(10, 9, 255, 0, 0, 255)
//            val hash1 = ThumbHash.rgbaToThumbHash(10, 9, rgba1)
//            val ratio1 = ThumbHash.thumbHashToApproximateAspectRatio(hash1)
//
//            // Should be close to 10:9 ratio
//            val expected = 10f / 9f
//            val epsilon = 0.25f  // Allow some error due to how ThumbHash works
//            kotlin.math.abs(ratio1 - expected) < epsilon shouldBe true
//        }
//
//        // More rectangular
//        run {
//            val rgba2 = createFlatColorImage(15, 10, 0, 255, 0, 255)
//            val hash2 = ThumbHash.rgbaToThumbHash(15, 10, rgba2)
//            val ratio2 = ThumbHash.thumbHashToApproximateAspectRatio(hash2)
//
//            // Should be close to 15:10 ratio
//            val expected = 15f / 10f
//            val epsilon = 0.25f
//            kotlin.math.abs(ratio2 - expected) < epsilon shouldBe true
//        }
//    }
//
//    "Checkerboard pattern should be visually consistent between implementations" {
//        val width = 64
//        val height = 64
//        val checkerboard = createCheckerboard(width, height, 8)
//
//        // Encode with both implementations
//        val kotlinHash = ThumbHash.rgbaToThumbHash(width, height, checkerboard)
//        val javaHash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, checkerboard)
//
//        // Decode with both implementations
//        val kotlinImage = ThumbHash.thumbHashToRGBA(kotlinHash)
//        val javaImage = com.madebyevan.thumbhash.ThumbHash.thumbHashToRGBA(javaHash)
//
//        // Both implementations should generate the same size output
//        kotlinImage.width shouldBe javaImage.width
//        kotlinImage.height shouldBe javaImage.height
//
//        // Calculate Mean Squared Error between the two images
//        val mse = calculateMSE(kotlinImage.rgba, javaImage.rgba)
//
//        // MSE should be very small (implementations should produce nearly identical output)
//        mse < 1.0 shouldBe true
//    }
//
//    "Gradient pattern should be visually consistent between implementations" {
//        val width = 80
//        val height = 60
//        val gradient = createGradient(width, height)
//
//        // Encode with both implementations
//        val kotlinHash = ThumbHash.rgbaToThumbHash(width, height, gradient)
//        val javaHash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, gradient)
//
//        // Decode with both implementations
//        val kotlinImage = ThumbHash.thumbHashToRGBA(kotlinHash)
//        val javaImage = com.madebyevan.thumbhash.ThumbHash.thumbHashToRGBA(javaHash)
//
//        // Both implementations should generate the same size output
//        kotlinImage.width shouldBe javaImage.width
//        kotlinImage.height shouldBe javaImage.height
//
//        // Calculate Mean Squared Error between the two images
//        val mse = calculateMSE(kotlinImage.rgba, javaImage.rgba)
//
//        // MSE should be very small (implementations should produce nearly identical output)
//        mse < 1.0 shouldBe true
//    }
//
//    "Circular pattern should be visually consistent between implementations" {
//        val width = 96
//        val height = 96
//        val circularPattern = createCircularPattern(width, height)
//
//        // Encode with both implementations
//        val kotlinHash = ThumbHash.rgbaToThumbHash(width, height, circularPattern)
//        val javaHash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, circularPattern)
//
//        // Decode with both implementations
//        val kotlinImage = ThumbHash.thumbHashToRGBA(kotlinHash)
//        val javaImage = com.madebyevan.thumbhash.ThumbHash.thumbHashToRGBA(javaHash)
//
//        // Both implementations should generate the same size output
//        kotlinImage.width shouldBe javaImage.width
//        kotlinImage.height shouldBe javaImage.height
//
//        // Calculate Mean Squared Error between the two images
//        val mse = calculateMSE(kotlinImage.rgba, javaImage.rgba)
//
//        // MSE should be very small (implementations should produce nearly identical output)
//        mse < 1.0 shouldBe true
//    }
//
//    "Transparency pattern should be visually consistent between implementations" {
//        val width = 72
//        val height = 72
//        val transparencyPattern = createTransparencyPattern(width, height)
//
//        // Encode with both implementations
//        val kotlinHash = ThumbHash.rgbaToThumbHash(width, height, transparencyPattern)
//        val javaHash = com.madebyevan.thumbhash.ThumbHash.rgbaToThumbHash(width, height, transparencyPattern)
//
//        // Decode with both implementations
//        val kotlinImage = ThumbHash.thumbHashToRGBA(kotlinHash)
//        val javaImage = com.madebyevan.thumbhash.ThumbHash.thumbHashToRGBA(javaHash)
//
//        // Both implementations should generate the same size output
//        kotlinImage.width shouldBe javaImage.width
//        kotlinImage.height shouldBe javaImage.height
//
//        // Calculate Mean Squared Error between the two images
//        val mse = calculateMSE(kotlinImage.rgba, javaImage.rgba)
//
//        // MSE should be very small (implementations should produce nearly identical output)
//        mse < 1.0 shouldBe true
//    }
})
