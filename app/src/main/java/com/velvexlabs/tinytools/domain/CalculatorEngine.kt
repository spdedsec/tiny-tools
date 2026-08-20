package com.velvexlabs.tinytools.domain

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

object CalculatorEngine {
    fun evaluate(expression: String): Double {
        val normalized = expression.replace("%", "/100")
        require(normalized.isNotBlank()) { "Enter a calculation." }
        return Parser(normalized).parse().also { require(it.isFinite()) { "The result is not finite." } }
    }

    fun format(value: Double): String {
        if (abs(value) < 0.0000000001) return "0"
        return BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString()
    }

    private class Parser(private val input: String) {
        private var position = 0

        fun parse(): Double {
            val result = parseExpression()
            skipSpaces()
            require(position == input.length) { "Check the expression." }
            return result
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                skipSpaces()
                value = when {
                    match('+') -> value + parseTerm()
                    match('-') -> value - parseTerm()
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (true) {
                skipSpaces()
                value = when {
                    match('*') -> value * parseFactor()
                    match('/') -> value / parseFactor()
                    else -> return value
                }
            }
        }

        private fun parseFactor(): Double {
            skipSpaces()
            if (match('+')) return parseFactor()
            if (match('-')) return -parseFactor()
            if (match('(')) {
                val value = parseExpression()
                require(match(')')) { "Close the parentheses." }
                return value
            }
            val start = position
            while (position < input.length && (input[position].isDigit() || input[position] == '.')) position++
            require(position > start) { "Enter a valid number." }
            return input.substring(start, position).toDoubleOrNull() ?: error("Enter a valid number.")
        }

        private fun match(character: Char): Boolean {
            if (position < input.length && input[position] == character) {
                position++
                return true
            }
            return false
        }

        private fun skipSpaces() {
            while (position < input.length && input[position].isWhitespace()) position++
        }
    }
}

data class PercentageResult(
    val primary: Double,
    val detail: List<Pair<String, Double>>
)

object PercentageEngine {
    fun percentageOf(percent: Double, value: Double) = PercentageResult(percent * value / 100.0, listOf("Percentage" to percent, "Base" to value))
    fun whatPercentage(part: Double, whole: Double): PercentageResult {
        require(whole != 0.0) { "The whole value cannot be zero." }
        return PercentageResult(part / whole * 100.0, listOf("Part" to part, "Whole" to whole))
    }
    fun increase(value: Double, percent: Double) = PercentageResult(value * (1 + percent / 100.0), listOf("Original" to value, "Increase" to value * percent / 100.0))
    fun decrease(value: Double, percent: Double) = PercentageResult(value * (1 - percent / 100.0), listOf("Original" to value, "Reduction" to value * percent / 100.0))
    fun discount(original: Double, percent: Double) = PercentageResult(original * (1 - percent / 100.0), listOf("Original" to original, "Discount" to original * percent / 100.0))
    fun markup(cost: Double, percent: Double) = PercentageResult(cost * (1 + percent / 100.0), listOf("Cost" to cost, "Markup" to cost * percent / 100.0))
    fun reverse(finalValue: Double, percent: Double): PercentageResult {
        require(percent < 100) { "The percentage must be below 100." }
        return PercentageResult(finalValue / (1 - percent / 100.0), listOf("Final" to finalValue, "Discount" to percent))
    }
}
