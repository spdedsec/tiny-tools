package com.velvexlabs.tinytools.domain

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlin.math.pow

object QrEngine {
    fun generate(content: String, size: Int = 768): Bitmap {
        require(content.isNotBlank()) { "Enter content to encode." }
        val hints = mapOf<EncodeHintType, Any>(EncodeHintType.MARGIN to 2, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M)
        val matrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bitmap ->
            for (x in 0 until size) for (y in 0 until size) bitmap.setPixel(x, y, if (matrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }
}

data class ColorInfo(val hex: String, val red: Int, val green: Int, val blue: Int, val alpha: Int, val complementary: String, val luminance: Double, val contrastWithWhite: Double, val contrastWithBlack: Double)

object ColorEngine {
    fun inspect(input: String): ColorInfo {
        val normalized = input.trim().removePrefix("#")
        require(normalized.length == 6 || normalized.length == 8) { "Use a hex color such as #F28C28." }
        require(normalized.all { it in "0123456789abcdefABCDEF" }) { "Use a hex color such as #F28C28." }
        val alpha = if (normalized.length == 8) normalized.substring(0, 2).toInt(16) else 255
        val offset = if (normalized.length == 8) 2 else 0
        val red = normalized.substring(offset, offset + 2).toInt(16)
        val green = normalized.substring(offset + 2, offset + 4).toInt(16)
        val blue = normalized.substring(offset + 4, offset + 6).toInt(16)
        val luminance = relativeLuminance(red, green, blue)
        val complementary = String.format("#%02X%02X%02X", 255 - red, 255 - green, 255 - blue)
        return ColorInfo(String.format("#%02X%02X%02X", red, green, blue), red, green, blue, alpha, complementary, luminance, contrastRatio(luminance, 1.0), contrastRatio(luminance, 0.0))
    }

    private fun relativeLuminance(red: Int, green: Int, blue: Int): Double {
        fun channel(value: Int): Double { val normalized = value / 255.0; return if (normalized <= 0.03928) normalized / 12.92 else ((normalized + 0.055) / 1.055).pow(2.4) }
        return 0.2126 * channel(red) + 0.7152 * channel(green) + 0.0722 * channel(blue)
    }

    private fun contrastRatio(first: Double, second: Double): Double = (maxOf(first, second) + 0.05) / (minOf(first, second) + 0.05)
}

