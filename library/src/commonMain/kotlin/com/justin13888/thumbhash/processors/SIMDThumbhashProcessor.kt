package com.justin13888.thumbhash.processors

import com.justin13888.thumbhash.ThumbHash
import com.justin13888.thumbhash.ThumbHash.Image
import com.justin13888.thumbhash.ThumbHash.RGBA
import kotlin.math.roundToInt

// TODO: Implement actual SIMD by platform

class SIMDThumbHashProcessor : StandardThumbHashProcessor()