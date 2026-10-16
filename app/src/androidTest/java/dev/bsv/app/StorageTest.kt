package dev.bsv.app

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.bsv.settings.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageTest {
    @Test
    fun dataStorePersistsAndRestoresPreferences() = runBlocking {
        val repo = DataStoreSettingsRepository(ApplicationProvider.getApplicationContext())
        val previous = repo.settings.first()
        try {
            val expected =
                Settings(
                    language = "ru",
                    customRank = true,
                    rank = "Legend",
                    buttonX = .2f,
                    buttonY = .3f,
                    enabled = setOf("Hit Effects"),
                )
            repo.save(expected)
            val reopened = DataStoreSettingsRepository(ApplicationProvider.getApplicationContext())
            assertEquals(expected, reopened.settings.first())
            repo.save(Settings())
            assertEquals(Settings(), reopened.settings.first())
        } finally {
            repo.save(previous)
        }
    }
}
