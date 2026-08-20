package com.velvexlabs.tinytools

import com.velvexlabs.tinytools.domain.ConversionEngine
import com.velvexlabs.tinytools.domain.DateEngine
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class DateConversionEngineTest {
    @Test fun convertsKilometersToMeters() {
        val group = ConversionEngine.groups.first { it.name == "Length" }
        val kilometers = group.units.first { it.symbol == "km" }
        val meters = group.units.first { it.symbol == "m" }
        assertEquals(2500.0, ConversionEngine.convert(2.5, kilometers, meters), 0.000001)
    }

    @Test fun convertsTemperature() {
        val group = ConversionEngine.groups.first { it.name == "Temperature" }
        val celsius = group.units.first { it.symbol == "°C" }
        val fahrenheit = group.units.first { it.symbol == "°F" }
        assertEquals(212.0, ConversionEngine.convert(100.0, celsius, fahrenheit), 0.000001)
    }

    @Test fun parsesPracticalDurations() {
        assertEquals(9000L, ConversionEngine.parseDuration("2h 30m"))
        assertEquals("2h 30m", ConversionEngine.formatDuration(9000L))
    }

    @Test fun calculatesAgeOnLeapDay() {
        val period = DateEngine.age(LocalDate.parse("2000-02-29"), LocalDate.parse("2024-02-29"))
        assertEquals(24, period.years)
        assertEquals(0, period.months)
        assertEquals(0, period.days)
    }

    @Test fun handlesLeapDayBirthdayInNonLeapYear() {
        assertEquals(LocalDate.parse("2025-02-28"), DateEngine.nextBirthday(LocalDate.parse("2000-02-29"), LocalDate.parse("2025-01-01")))
    }

    @Test fun calculatesDateDifference() {
        val difference = DateEngine.dateDifference(LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-31"))
        assertEquals(30L, difference.totalDays)
    }

    @Test fun calculatesCountdown() {
        val result = DateEngine.countdown(LocalDateTime.of(2026, 1, 2, 1, 2), LocalDateTime.of(2026, 1, 1, 0, 0))
        assertEquals(1L, result.days)
        assertEquals(1L, result.hours)
        assertEquals(2L, result.minutes)
    }
}
