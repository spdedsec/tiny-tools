package com.velvexlabs.tinytools

import com.velvexlabs.tinytools.domain.CalculatorEngine
import com.velvexlabs.tinytools.domain.PercentageEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorEngineTest {
    @Test fun evaluatesOperatorPrecedence() {
        assertEquals(14.0, CalculatorEngine.evaluate("2 + 3 * 4"), 0.000001)
    }

    @Test fun evaluatesParenthesesAndNegativeValues() {
        assertEquals(-12.5, CalculatorEngine.evaluate("-(2 + 3) * 2.5"), 0.000001)
    }

    @Test fun evaluatesPercentAsFraction() {
        assertEquals(5.0, CalculatorEngine.evaluate("20% * 25"), 0.000001)
    }

    @Test fun formatsWithoutUnnecessaryZeros() {
        assertEquals("12.5", CalculatorEngine.format(12.50000000001))
        assertEquals("0", CalculatorEngine.format(-0.00000000001))
    }

    @Test fun calculatesPercentageOf() {
        assertEquals(100.0, PercentageEngine.percentageOf(20.0, 500.0).primary, 0.000001)
    }

    @Test fun calculatesIncreaseAndDiscount() {
        assertEquals(600.0, PercentageEngine.increase(500.0, 20.0).primary, 0.000001)
        assertEquals(400.0, PercentageEngine.discount(500.0, 20.0).primary, 0.000001)
    }

    @Test fun reversesDiscount() {
        assertEquals(500.0, PercentageEngine.reverse(400.0, 20.0).primary, 0.000001)
    }
}
