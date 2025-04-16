package com.justin13888.thumbhash.processors

import com.justin13888.thumbhash.ThumbHash
import com.justin13888.thumbhash.ThumbHash.Image
import com.justin13888.thumbhash.ThumbHash.RGBA
import kotlin.math.roundToInt

open class StandardThumbHashProcessor : ThumbHashProcessor() {
    override fun rgbaToThumbHash(
        w: Int,
        h: Int,
        rgba: ByteArray,
    ): ByteArray {
        // Encoding an image larger than MAX_SIZExMAX_SIZE is slow with no benefit
        require(!(w > MAX_SIZE || h > MAX_SIZE)) { "${w}x$h doesn't fit in ${MAX_SIZE}x${MAX_SIZE}" }

        // Validate that the rgba array has the expected length
        require(rgba.size == w * h * 4) { "RGBA array must have w*h*4 elements (expected ${w * h * 4}, got ${rgba.size})" }

        // Determine the average color
        var avgR = 0f
        var avgG = 0f
        var avgB = 0f
        var avgA = 0f

        for (i in 0 until w * h) {
            val j = i * 4
            val alpha = (rgba[j + 3].toInt() and 255) / 255.0f
            avgR += alpha / 255.0f * (rgba[j].toInt() and 255)
            avgG += alpha / 255.0f * (rgba[j + 1].toInt() and 255)
            avgB += alpha / 255.0f * (rgba[j + 2].toInt() and 255)
            avgA += alpha
        }

        if (avgA > 0) {
            avgR /= avgA
            avgG /= avgA
            avgB /= avgA
        }

        val hasAlpha = avgA < w * h
        val lLimit = if (hasAlpha) 5 else 7 // Use fewer luminance bits if there's alpha
        val lx = maxOf(1, ((lLimit * w).toFloat() / maxOf(w, h).toFloat()).roundToInt())
        val ly = maxOf(1, ((lLimit * h).toFloat() / maxOf(w, h).toFloat()).roundToInt())

        val l = FloatArray(w * h) // luminance
        val p = FloatArray(w * h) // yellow - blue
        val q = FloatArray(w * h) // red - green
        val a = FloatArray(w * h) // alpha

        // Convert the image from RGBA to LPQA (composite atop the average color)
        for (i in 0 until w * h) {
            val j = i * 4
            val alpha = (rgba[j + 3].toInt() and 255) / 255.0f
            val r = avgR * (1.0f - alpha) + alpha / 255.0f * (rgba[j].toInt() and 255)
            val g = avgG * (1.0f - alpha) + alpha / 255.0f * (rgba[j + 1].toInt() and 255)
            val b = avgB * (1.0f - alpha) + alpha / 255.0f * (rgba[j + 2].toInt() and 255)
            l[i] = (r + g + b) / 3.0f
            p[i] = (r + g) / 2.0f - b
            q[i] = r - g
            a[i] = alpha
        }

        // Encode using the DCT into DC (constant) and normalized AC (varying) terms
        val lChannel = Channel(maxOf(3, lx), maxOf(3, ly)).encode(w, h, l)
        val pChannel = Channel(3, 3).encode(w, h, p)
        val qChannel = Channel(3, 3).encode(w, h, q)
        val aChannel = if (hasAlpha) Channel(5, 5).encode(w, h, a) else null

        // Write the constants
        val isLandscape = w > h
        val header24 =
            (63.0f * lChannel.dc).roundToInt() or
                ((31.5f + 31.5f * pChannel.dc).roundToInt() shl 6) or
                ((31.5f + 31.5f * qChannel.dc).roundToInt() shl 12) or
                ((31.0f * lChannel.scale).roundToInt() shl 18) or
                (if (hasAlpha) 1 shl 23 else 0)
        val header16 =
            (if (isLandscape) ly else lx) or
                ((63.0f * pChannel.scale).roundToInt() shl 3) or
                ((63.0f * qChannel.scale).roundToInt() shl 9) or
                (if (isLandscape) 1 shl 15 else 0)
        val acStart = if (hasAlpha) 6 else 5
        val acCount =
            lChannel.ac.size + pChannel.ac.size + qChannel.ac.size +
                (if (hasAlpha) aChannel!!.ac.size else 0)
        val hash = ByteArray(acStart + (acCount + 1) / 2)
        hash[0] = header24.toByte()
        hash[1] = (header24 shr 8).toByte()
        hash[2] = (header24 shr 16).toByte()
        hash[3] = header16.toByte()
        hash[4] = (header16 shr 8).toByte()

        if (hasAlpha) {
            hash[5] =
                (
                    (15.0f * aChannel!!.dc).roundToInt() or
                        ((15.0f * aChannel.scale).roundToInt() shl 4)
                ).toByte()
        }

        // Write the varying factors
        var acIndex = 0
        acIndex = lChannel.writeTo(hash, acStart, acIndex)
        acIndex = pChannel.writeTo(hash, acStart, acIndex)
        acIndex = qChannel.writeTo(hash, acStart, acIndex)
        if (hasAlpha) aChannel!!.writeTo(hash, acStart, acIndex)

        return hash
    }

    override fun thumbHashToRGBA(hash: ByteArray): Image {
        // Read the constants
        val header24 =
            (hash[0].toInt() and 255) or
                ((hash[1].toInt() and 255) shl 8) or
                ((hash[2].toInt() and 255) shl 16)
        val header16 = (hash[3].toInt() and 255) or ((hash[4].toInt() and 255) shl 8)
        val lDc = (header24 and 63) / 63.0f
        val pDc = ((header24 shr 6) and 63) / 31.5f - 1.0f
        val qDc = ((header24 shr 12) and 63) / 31.5f - 1.0f
        val lScale = ((header24 shr 18) and 31) / 31.0f
        val hasAlpha = (header24 shr 23) != 0
        val pScale = ((header16 shr 3) and 63) / 63.0f
        val qScale = ((header16 shr 9) and 63) / 63.0f
        val isLandscape = (header16 shr 15) != 0

        val lx = maxOf(3, if (isLandscape) (if (hasAlpha) 5 else 7) else header16 and 7)
        val ly = maxOf(3, if (isLandscape) (header16 and 7) else (if (hasAlpha) 5 else 7))
        val aDc = if (hasAlpha) (hash[5].toInt() and 15) / 15.0f else 1.0f
        val aScale = ((hash[5].toInt() shr 4) and 15) / 15.0f

        // Read the varying factors (boost saturation by 1.25x to compensate for quantization)
        val acStart = if (hasAlpha) 6 else 5
        var acIndex = 0
        val lChannel = Channel(lx, ly)
        val pChannel = Channel(3, 3)
        val qChannel = Channel(3, 3)
        var aChannel: Channel? = null

        acIndex = lChannel.decode(hash, acStart, acIndex, lScale)
        acIndex = pChannel.decode(hash, acStart, acIndex, pScale * 1.25f)
        acIndex = qChannel.decode(hash, acStart, acIndex, qScale * 1.25f)

        if (hasAlpha) {
            aChannel = Channel(5, 5)
            aChannel.decode(hash, acStart, acIndex, aScale)
        }

        val lAc = lChannel.ac
        val pAc = pChannel.ac
        val qAc = qChannel.ac
        val aAc = if (hasAlpha) aChannel!!.ac else null

        // Decode using the DCT into RGB
        val ratio = thumbHashToApproximateAspectRatio(hash)
        val w = (if (ratio > 1.0f) 32.0f else 32.0f * ratio).roundToInt()
        val h = (if (ratio > 1.0f) 32.0f / ratio else 32.0f).roundToInt()
        val rgba = ByteArray(w * h * 4)
        val cxStop = maxOf(lx, if (hasAlpha) 5 else 3)
        val cyStop = maxOf(ly, if (hasAlpha) 5 else 3)
        val fx = FloatArray(cxStop)
        val fy = FloatArray(cyStop)

        for (y in 0 until h) {
            for (x in 0 until w) {
                val i = (y * w + x) * 4
                var l = lDc
                var p = pDc
                var q = qDc
                var a = aDc

                // Precompute the coefficients
                for (cx in 0 until cxStop) {
                    fx[cx] = kotlin.math.cos(kotlin.math.PI / w * (x + 0.5f) * cx).toFloat()
                }
                for (cy in 0 until cyStop) {
                    fy[cy] = kotlin.math.cos(kotlin.math.PI / h * (y + 0.5f) * cy).toFloat()
                }

                // Decode L
                var j = 0
                for (cy in 0 until ly) {
                    val fy2 = fy[cy] * 2.0f
                    val cxStart = if (cy > 0) 0 else 1
                    for (cx in cxStart until lx * (ly - cy) / ly) {
                        l += lAc[j] * fx[cx] * fy2
                        j++
                    }
                }

                // Decode P and Q
                j = 0
                for (cy in 0 until 3) {
                    val fy2 = fy[cy] * 2.0f
                    val cxStart = if (cy > 0) 0 else 1
                    for (cx in cxStart until 3 - cy) {
                        val f = fx[cx] * fy2
                        p += pAc[j] * f
                        q += qAc[j] * f
                        j++
                    }
                }

                // Decode A
                if (hasAlpha) {
                    j = 0
                    for (cy in 0 until 5) {
                        val fy2 = fy[cy] * 2.0f
                        val cxStart = if (cy > 0) 0 else 1
                        for (cx in cxStart until 5 - cy) {
                            a += aAc!![j] * fx[cx] * fy2
                            j++
                        }
                    }
                }

                // Convert to RGB
                val b = l - 2.0f / 3.0f * p
                val r = (3.0f * l - b + q) / 2.0f
                val g = r - q

                rgba[i] = maxOf(0, minOf(255, (255.0f * minOf(1f, r)).roundToInt())).toByte()
                rgba[i + 1] = maxOf(0, minOf(255, (255.0f * minOf(1f, g)).roundToInt())).toByte()
                rgba[i + 2] = maxOf(0, minOf(255, (255.0f * minOf(1f, b)).roundToInt())).toByte()
                rgba[i + 3] = maxOf(0, minOf(255, (255.0f * minOf(1f, a)).roundToInt())).toByte()
            }
        }
        return ThumbHash.Image(w, h, rgba)
    }

    override fun thumbHashToAverageRGBA(hash: ByteArray): RGBA {
        val header =
            (hash[0].toInt() and 255) or
                ((hash[1].toInt() and 255) shl 8) or
                ((hash[2].toInt() and 255) shl 16)
        val l = (header and 63) / 63.0f
        val p = ((header shr 6) and 63) / 31.5f - 1.0f
        val q = ((header shr 12) and 63) / 31.5f - 1.0f
        val hasAlpha = (header shr 23) != 0
        val a = if (hasAlpha) (hash[5].toInt() and 15) / 15.0f else 1.0f
        val b = l - 2.0f / 3.0f * p
        val r = (3.0f * l - b + q) / 2.0f
        val g = r - q

        return RGBA(
            maxOf(0f, minOf(1f, r)),
            maxOf(0f, minOf(1f, g)),
            maxOf(0f, minOf(1f, b)),
            a,
        )
    }

    override fun thumbHashToApproximateAspectRatio(hash: ByteArray): Float {
        val headerByte = hash[3].toInt()
        val hasAlpha = (hash[2].toInt() and 0x80) != 0
        val isLandscape = (hash[4].toInt() and 0x80) != 0
        val lx = if (isLandscape) (if (hasAlpha) 5 else 7) else headerByte and 7
        val ly = if (isLandscape) (headerByte and 7) else (if (hasAlpha) 5 else 7)
        return lx.toFloat() / ly.toFloat()
    }

    /**
     * Internal helper class for encoding/decoding frequency components.
     */
    private class Channel(
        val nx: Int,
        val ny: Int,
    ) {
        var dc: Float = 0f
        val ac: FloatArray
        var scale: Float = 0f

        init {
            var n = 0
            for (cy in 0 until ny) {
                for (cx in (if (cy > 0) 0 else 1) until nx * (ny - cy) / ny) {
                    n++
                }
            }
            ac = FloatArray(n)
        }

        fun encode(
            w: Int,
            h: Int,
            channel: FloatArray,
        ): Channel {
            var n = 0
            val fx = FloatArray(w)

            for (cy in 0 until ny) {
                for (cx in 0 until nx * (ny - cy) / ny) {
                    if (cx == 0 && cy == 0) {
                        // Skip the DC component (handled separately)
                        continue
                    }

                    var f = 0f
                    for (x in 0 until w) {
                        fx[x] = kotlin.math.cos(kotlin.math.PI / w * cx * (x + 0.5f)).toFloat()
                    }

                    for (y in 0 until h) {
                        val fy = kotlin.math.cos(kotlin.math.PI / h * cy * (y + 0.5f)).toFloat()

                        for (x in 0 until w) {
                            f += channel[x + y * w] * fx[x] * fy
                        }
                    }

                    f /= (w * h).toFloat()
                    ac[n++] = f
                    scale = maxOf(scale, kotlin.math.abs(f))
                }
            }

            // Calculate DC component (average)
            for (i in 0 until w * h) {
                dc += channel[i]
            }
            dc /= (w * h).toFloat()

            if (scale > 0) {
                for (i in ac.indices) {
                    ac[i] = 0.5f + 0.5f / scale * ac[i]
                }
            }

            return this
        }

        fun decode(
            hash: ByteArray,
            start: Int,
            index: Int,
            scale: Float,
        ): Int {
            var idx = index
            for (i in ac.indices) {
                val data = hash[start + (idx shr 1)].toInt() shr ((idx and 1) shl 2)
                ac[i] = ((data and 15) / 7.5f - 1.0f) * scale
                idx++
            }
            return idx
        }

        fun writeTo(
            hash: ByteArray,
            start: Int,
            index: Int,
        ): Int {
            var idx = index
            for (v in ac) {
                val byteIndex = start + (idx shr 1)
                val shift = (idx and 1) shl 2
                hash[byteIndex] = (hash[byteIndex].toInt() or ((15.0f * v).roundToInt() shl shift)).toByte()
                idx++
            }
            return idx
        }
    }
}
