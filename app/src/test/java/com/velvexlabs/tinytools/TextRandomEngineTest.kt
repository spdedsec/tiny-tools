package com.velvexlabs.tinytools

import com.velvexlabs.tinytools.domain.CaseMode
import com.velvexlabs.tinytools.domain.RandomToolsEngine
import com.velvexlabs.tinytools.domain.TextToolsEngine
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class TextRandomEngineTest {
    @Test fun countsText() {
        val stats = TextToolsEngine.count("Hello world\nAgain")
        assertEquals(17, stats.characters)
        assertEquals(15, stats.charactersNoSpaces)
        assertEquals(3, stats.words)
        assertEquals(2, stats.lines)
    }

    @Test fun cleansAndDeduplicatesLines() {
        assertEquals("one\ntwo", TextToolsEngine.clean(" one  \n two\n two ", deduplicateLines = true))
    }

    @Test fun convertsCommonCases() {
        assertEquals("HELLO WORLD", TextToolsEngine.convertCase("Hello world", CaseMode.UPPER))
        assertEquals("helloWorld", TextToolsEngine.convertCase("Hello world", CaseMode.CAMEL))
        assertEquals("hello-world", TextToolsEngine.convertCase("Hello world", CaseMode.KEBAB))
    }

    @Test fun picksFromOptionsDeterministicallyWithSeed() {
        assertEquals("a", RandomToolsEngine.pick("a\nb\nc", Random(1)))
    }
}
