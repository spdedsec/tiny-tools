package com.velvexlabs.tinytools.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TextFields

/** A deliberately small surface model; complexity stays in the feature implementations. */
data class ToolDefinition(
    val id: String,
    val title: String,
    val category: String,
    val aliases: List<String>,
    val icon: ImageVector
)

val tinyToolCatalog = listOf(
    ToolDefinition("calculator", "Calculator", "CALCULATE", listOf("calc", "math", "arithmetic"), Icons.Outlined.Calculate),
    ToolDefinition("percentage", "Percentage", "CALCULATE", listOf("percent", "%", "tax", "gst", "discount", "markup"), Icons.Outlined.Percent),
    ToolDefinition("tip", "Tip", "CALCULATE", listOf("gratuity", "service", "restaurant"), Icons.Outlined.ReceiptLong),
    ToolDefinition("split_bill", "Split Bill", "CALCULATE", listOf("split", "bill", "people", "share"), Icons.Outlined.ReceiptLong),
    ToolDefinition("units", "Units", "CONVERT", listOf("unit", "kg", "mph", "length", "weight", "temperature"), Icons.Outlined.Calculate),
    ToolDefinition("time", "Time", "CONVERT", listOf("time", "seconds", "minutes", "hours"), Icons.Outlined.Schedule),
    ToolDefinition("age", "Age", "DATES", listOf("age", "birthday", "years", "date of birth"), Icons.Outlined.Schedule),
    ToolDefinition("date_difference", "Date Difference", "DATES", listOf("date", "days", "between", "duration"), Icons.Outlined.Schedule),
    ToolDefinition("countdown", "Countdown", "DATES", listOf("countdown", "deadline", "timer"), Icons.Outlined.Schedule),
    ToolDefinition("count", "Count", "TEXT", listOf("text", "characters", "words", "lines"), Icons.Outlined.TextFields),
    ToolDefinition("clean", "Clean", "TEXT", listOf("text", "spaces", "lines", "trim"), Icons.Outlined.CleaningServices),
    ToolDefinition("case", "Case", "TEXT", listOf("uppercase", "lowercase", "camel", "snake", "kebab"), Icons.Outlined.TextFields),
    ToolDefinition("random", "Random Picker", "RANDOM", listOf("random", "choose", "pick", "list"), Icons.Outlined.TextFields),
    ToolDefinition("decision", "Decision Maker", "RANDOM", listOf("yes", "no", "decision", "choose"), Icons.Outlined.TextFields),
    ToolDefinition("qr", "QR Code", "OTHER", listOf("qr", "barcode", "link", "scan"), Icons.Outlined.TextFields),
    ToolDefinition("color", "Color", "OTHER", listOf("color", "hex", "rgb", "contrast", "palette"), Icons.Outlined.TextFields),
)
