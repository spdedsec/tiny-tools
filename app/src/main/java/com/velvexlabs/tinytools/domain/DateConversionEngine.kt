package com.velvexlabs.tinytools.domain

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlin.math.roundToLong

sealed class UnitSpec(val label: String, val symbol: String) {
    class Linear(label: String, symbol: String, val toBase: Double) : UnitSpec(label, symbol)
    class Temperature(label: String, symbol: String) : UnitSpec(label, symbol)
}

data class UnitGroup(val name: String, val units: List<UnitSpec>)

object ConversionEngine {
    val groups = listOf(
        UnitGroup("Length", listOf(UnitSpec.Linear("Meter", "m", 1.0), UnitSpec.Linear("Kilometer", "km", 1000.0), UnitSpec.Linear("Centimeter", "cm", 0.01), UnitSpec.Linear("Mile", "mi", 1609.344), UnitSpec.Linear("Foot", "ft", 0.3048), UnitSpec.Linear("Inch", "in", 0.0254))),
        UnitGroup("Area", listOf(UnitSpec.Linear("Square meter", "m²", 1.0), UnitSpec.Linear("Square kilometer", "km²", 1_000_000.0), UnitSpec.Linear("Square foot", "ft²", 0.09290304), UnitSpec.Linear("Acre", "acre", 4046.8564224))),
        UnitGroup("Mass", listOf(UnitSpec.Linear("Gram", "g", 1.0), UnitSpec.Linear("Kilogram", "kg", 1000.0), UnitSpec.Linear("Pound", "lb", 453.59237), UnitSpec.Linear("Ounce", "oz", 28.349523125))),
        UnitGroup("Temperature", listOf(UnitSpec.Temperature("Celsius", "°C"), UnitSpec.Temperature("Fahrenheit", "°F"), UnitSpec.Temperature("Kelvin", "K"))),
        UnitGroup("Volume", listOf(UnitSpec.Linear("Liter", "L", 1.0), UnitSpec.Linear("Milliliter", "mL", 0.001), UnitSpec.Linear("US gallon", "gal", 3.785411784), UnitSpec.Linear("Cubic meter", "m³", 1000.0))),
        UnitGroup("Speed", listOf(UnitSpec.Linear("Meters per second", "m/s", 1.0), UnitSpec.Linear("Kilometers per hour", "km/h", 0.2777777778), UnitSpec.Linear("Miles per hour", "mph", 0.44704))),
        UnitGroup("Pressure", listOf(UnitSpec.Linear("Pascal", "Pa", 1.0), UnitSpec.Linear("Bar", "bar", 100000.0), UnitSpec.Linear("Atmosphere", "atm", 101325.0), UnitSpec.Linear("PSI", "psi", 6894.7572932))),
        UnitGroup("Energy", listOf(UnitSpec.Linear("Joule", "J", 1.0), UnitSpec.Linear("Kilojoule", "kJ", 1000.0), UnitSpec.Linear("Calorie", "cal", 4.184), UnitSpec.Linear("Kilowatt-hour", "kWh", 3_600_000.0))),
        UnitGroup("Power", listOf(UnitSpec.Linear("Watt", "W", 1.0), UnitSpec.Linear("Kilowatt", "kW", 1000.0), UnitSpec.Linear("Horsepower", "hp", 745.699872))),
        UnitGroup("Frequency", listOf(UnitSpec.Linear("Hertz", "Hz", 1.0), UnitSpec.Linear("Kilohertz", "kHz", 1000.0), UnitSpec.Linear("Megahertz", "MHz", 1_000_000.0))),
        UnitGroup("Angle", listOf(UnitSpec.Linear("Degree", "°", 1.0), UnitSpec.Linear("Radian", "rad", 57.2957795131))),
        UnitGroup("Data", listOf(UnitSpec.Linear("Byte", "B", 1.0), UnitSpec.Linear("Kilobyte", "KB", 1024.0), UnitSpec.Linear("Megabyte", "MB", 1_048_576.0), UnitSpec.Linear("Gigabyte", "GB", 1_073_741_824.0)))
    )

    fun convert(value: Double, from: UnitSpec, to: UnitSpec): Double {
        if (from is UnitSpec.Temperature && to is UnitSpec.Temperature) {
            val celsius = when (from.symbol) { "°C" -> value; "°F" -> (value - 32) * 5 / 9; else -> value - 273.15 }
            return when (to.symbol) { "°C" -> celsius; "°F" -> celsius * 9 / 5 + 32; else -> celsius + 273.15 }
        }
        require(from is UnitSpec.Linear && to is UnitSpec.Linear) { "Choose units from the same category." }
        return value * from.toBase / to.toBase
    }

    fun parseDuration(text: String): Long {
        val compact = text.lowercase().replace(" ", "")
        val match = Regex("^(?:(\\d+(?:\\.\\d+)?)w)?(?:(\\d+(?:\\.\\d+)?)d)?(?:(\\d+(?:\\.\\d+)?)h)?(?:(\\d+(?:\\.\\d+)?)m)?(?:(\\d+(?:\\.\\d+)?)s)?$").matchEntire(compact)
            ?: error("Use a format such as 2h 30m.")
        val weeks = match.groupValues[1].toDoubleOrNull() ?: 0.0
        val days = match.groupValues[2].toDoubleOrNull() ?: 0.0
        val hours = match.groupValues[3].toDoubleOrNull() ?: 0.0
        val minutes = match.groupValues[4].toDoubleOrNull() ?: 0.0
        val seconds = match.groupValues[5].toDoubleOrNull() ?: 0.0
        require(weeks + days + hours + minutes + seconds > 0) { "Enter a duration." }
        return ((weeks * 604800 + days * 86400 + hours * 3600 + minutes * 60 + seconds)).roundToLong()
    }

    fun formatDuration(totalSeconds: Long): String {
        val sign = if (totalSeconds < 0) "-" else ""
        var remaining = kotlin.math.abs(totalSeconds)
        val weeks = remaining / 604800; remaining %= 604800
        val days = remaining / 86400; remaining %= 86400
        val hours = remaining / 3600; remaining %= 3600
        val minutes = remaining / 60; val seconds = remaining % 60
        return sign + listOfNotNull(
            weeks.takeIf { it > 0 }?.let { "${it}w" },
            days.takeIf { it > 0 }?.let { "${it}d" },
            hours.takeIf { it > 0 }?.let { "${it}h" },
            minutes.takeIf { it > 0 }?.let { "${it}m" },
            seconds.takeIf { it > 0 || totalSeconds == 0L }?.let { "${it}s" }
        ).joinToString(" ")
    }
}

object DateEngine {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun parseDate(value: String): LocalDate = try { LocalDate.parse(value, dateFormatter) } catch (_: DateTimeParseException) { error("Use YYYY-MM-DD.") }

    fun age(birth: LocalDate, onDate: LocalDate): Period {
        require(!birth.isAfter(onDate)) { "Date of birth must be before the selected date." }
        return Period.between(birth, onDate)
    }

    fun nextBirthday(birth: LocalDate, onDate: LocalDate): LocalDate {
        fun birthdayIn(year: Int): LocalDate {
            val day = minOf(birth.dayOfMonth, LocalDate.of(year, birth.month, 1).lengthOfMonth())
            return LocalDate.of(year, birth.month, day)
        }
        var next = birthdayIn(onDate.year)
        if (!next.isAfter(onDate)) next = birthdayIn(onDate.year + 1)
        return next
    }

    fun dateDifference(first: LocalDate, second: LocalDate): DateDifference {
        val start = minOf(first, second)
        val end = maxOf(first, second)
        return DateDifference(Period.between(start, end), ChronoUnit.DAYS.between(start, end))
    }

    fun countdown(target: LocalDateTime, now: LocalDateTime = LocalDateTime.now()): CountdownResult {
        val duration = Duration.between(now, target)
        val seconds = duration.seconds
        val remaining = kotlin.math.max(0L, seconds)
        return CountdownResult(remaining / 86400, (remaining % 86400) / 3600, (remaining % 3600) / 60, remaining % 60, seconds >= 0)
    }
}

data class DateDifference(val period: Period, val totalDays: Long)
data class CountdownResult(val days: Long, val hours: Long, val minutes: Long, val seconds: Long, val active: Boolean)
