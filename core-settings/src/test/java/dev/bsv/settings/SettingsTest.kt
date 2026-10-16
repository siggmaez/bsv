package dev.bsv.settings

import org.junit.Assert.*
import org.junit.Test

class SettingsTest {
    @Test
    fun roundTripPreservesPreferences() {
        val s =
            Settings(
                language = "ru",
                customRank = true,
                rank = "Legend",
                customTrophies = true,
                trophies = "12345",
                enabled = setOf("Attack FX"),
                intensity = mapOf("Attack FX" to .8f),
                buttonX = .25f,
            )
        assertEquals(s, SettingsCodec.decode(SettingsCodec.encode(s)))
    }

    @Test
    fun untrustedImportIsBounded() {
        val s =
            SettingsCodec.decode(
                """{"scale":99,"opacity":-2,"rank":"fake","enabled":["Attack FX","unknown"],"buttonX":-9,"trophies":"abc123"}"""
            )
        assertEquals(1.2f, s.scale)
        assertEquals(.65f, s.opacity)
        assertEquals(0f, s.buttonX)
        assertEquals("Diamond", s.rank)
        assertEquals(setOf("Attack FX"), s.enabled)
        assertEquals("123", s.trophies)
    }

    @Test
    fun defaultsAndForwardCompatibility() {
        assertEquals(Settings(), SettingsCodec.decode("""{"futureField":true}"""))
        assertEquals(0, Settings().activeCount)
        assertEquals(
            3,
            Settings(customRank = true, customTrophies = true, enabled = setOf("Hit Effects"))
                .activeCount,
        )
    }

    @Test(expected = Exception::class)
    fun rejectMalformedJson() {
        SettingsCodec.decode("not json")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectOversizedFile() {
        SettingsCodec.decode(" ".repeat(65537))
    }
}
