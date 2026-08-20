package com.velvexlabs.tinytools.domain

import kotlin.random.Random

object TextToolsEngine {
    fun count(text: String): TextStats {
        val words = Regex("\\S+").findAll(text).count()
        val lines = if (text.isEmpty()) 0 else text.lines().size
        val paragraphs = text.split(Regex("\\n\\s*\\n")).count { it.isNotBlank() }
        return TextStats(text.length, text.count { !it.isWhitespace() }, words, lines, paragraphs)
    }

    fun clean(text: String, trimLines: Boolean = true, collapseSpaces: Boolean = true, removeBlankLines: Boolean = false, deduplicateLines: Boolean = false): String {
        var lines = text.lines()
        if (trimLines) lines = lines.map { it.trim() }
        if (collapseSpaces) lines = lines.map { it.replace(Regex("[ \\t]+"), " ") }
        if (removeBlankLines) lines = lines.filter { it.isNotBlank() }
        if (deduplicateLines) lines = lines.distinct()
        return lines.joinToString("\n").trim()
    }

    fun convertCase(text: String, mode: CaseMode): String = when (mode) {
        CaseMode.UPPER -> text.uppercase()
        CaseMode.LOWER -> text.lowercase()
        CaseMode.SENTENCE -> text.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        CaseMode.TITLE -> text.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        CaseMode.CAMEL -> text.split(Regex("[^A-Za-z0-9]+" )).filter { it.isNotBlank() }.mapIndexed { index, word -> if (index == 0) word.lowercase() else word.lowercase().replaceFirstChar { it.titlecase() } }.joinToString("")
        CaseMode.SNAKE -> text.trim().split(Regex("[^A-Za-z0-9]+" )).filter { it.isNotBlank() }.joinToString("_") { it.lowercase() }
        CaseMode.KEBAB -> text.trim().split(Regex("[^A-Za-z0-9]+" )).filter { it.isNotBlank() }.joinToString("-") { it.lowercase() }
    }
}

data class TextStats(val characters: Int, val charactersNoSpaces: Int, val words: Int, val lines: Int, val paragraphs: Int)
enum class CaseMode(val label: String) { UPPER("UPPERCASE"), LOWER("lowercase"), SENTENCE("Sentence case"), TITLE("Title Case"), CAMEL("camelCase"), SNAKE("snake_case"), KEBAB("kebab-case") }

object RandomToolsEngine {
    fun pick(input: String, random: Random = Random.Default): String {
        val options = input.split(Regex("[\\n,]")).map { it.trim() }.filter { it.isNotBlank() }
        require(options.isNotEmpty()) { "Add at least two options." }
        return options[random.nextInt(options.size)]
    }

    fun decide(random: Random = Random.Default): String = if (random.nextBoolean()) "Yes" else "No"
}
