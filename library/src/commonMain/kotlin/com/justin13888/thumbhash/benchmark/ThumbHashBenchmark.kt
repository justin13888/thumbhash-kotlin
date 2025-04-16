package com.justin13888.thumbhash.benchmark

import com.justin13888.thumbhash.ThumbHash
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import kotlin.random.Random

@State(Scope.Benchmark)
@Warmup(iterations = 2)
@BenchmarkMode(Mode.Throughput)
class ThumbHashBenchmark {

    // Test images of different sizes
    private val smallImage = generateRandomImage(16, 16)
    private val mediumImage = generateRandomImage(50, 50)
    private val largeImage = generateRandomImage(100, 100)

    // Pre-encoded ThumbHashes for decoding benchmarks
    private val smallHash = ThumbHash.rgbaToThumbHash(smallImage.width, smallImage.height, smallImage.rgba)
    private val mediumHash = ThumbHash.rgbaToThumbHash(mediumImage.width, mediumImage.height, mediumImage.rgba)
    private val largeHash = ThumbHash.rgbaToThumbHash(largeImage.width, largeImage.height, largeImage.rgba)

    @Benchmark
    fun encodeSmallImage() {
        ThumbHash.rgbaToThumbHash(smallImage.width, smallImage.height, smallImage.rgba)
    }

    @Benchmark
    fun encodeMediumImage() {
        ThumbHash.rgbaToThumbHash(mediumImage.width, mediumImage.height, mediumImage.rgba)
    }

    @Benchmark
    fun encodeLargeImage() {
        ThumbHash.rgbaToThumbHash(largeImage.width, largeImage.height, largeImage.rgba)
    }

    @Benchmark
    fun decodeSmallHash() {
        ThumbHash.thumbHashToRGBA(smallHash)
    }

    @Benchmark
    fun decodeMediumHash() {
        ThumbHash.thumbHashToRGBA(mediumHash)
    }

    @Benchmark
    fun decodeLargeHash() {
        ThumbHash.thumbHashToRGBA(largeHash)
    }

    @Benchmark
    fun extractAverageColor() {
        ThumbHash.thumbHashToAverageRGBA(mediumHash)
    }

    @Benchmark
    fun extractAspectRatio() {
        ThumbHash.thumbHashToApproximateAspectRatio(mediumHash)
    }

    /**
     * Helper function to generate a random RGBA image for testing
     */
    private fun generateRandomImage(width: Int, height: Int): ThumbHash.Image {
        val rgba = ByteArray(width * height * 4)
        Random.nextBytes(rgba)
        return ThumbHash.Image(width, height, rgba)
    }
}