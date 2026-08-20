package com.velvexlabs.tinytools

import com.velvexlabs.tinytools.domain.ColorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QRColorEngineTest {
    @Test fun normalizesHexWithoutHash() {
        val info = ColorEngine.inspect("F28C28")
        assertEquals("#F28C28", info.hex)
        assertEquals(242, info.red)
        assertEquals(140, info.green)
        assertEquals(40, info.blue)
    }

    @Test fun calculatesComplementaryColor() {
        assertEquals("#00FF00", ColorEngine.inspect("#FF00FF").complementary)
    }

    @Test fun calculatesContrastRatios() {
        val black = ColorEngine.inspect("#000000")
        assertEquals(21.0, black.contrastWithWhite, 0.000001)
        assertTrue(black.contrastWithBlack >= 1.0)
    }
}
