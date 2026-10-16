package dev.bsv.app

import android.content.res.Configuration
import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @After
    fun releaseControlledFramesBeforeActivityClose() {
        // Stop the test window explicitly before closing it on legacy Android.
        // ActivityScenario maps CREATED to onStop and still verifies DESTROYED
        // during rule cleanup; no lifecycle assertion is skipped.
        compose.runOnIdle {
            compose.activity.window.setWindowAnimations(0)
            compose.activity.setContent {}
        }
        compose.waitForIdle()
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
    }

    @Test
    fun allTabsAndFloatingMenuSurviveRapidNavigation() {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithTag("nav-0").fetchSemanticsNodes().isNotEmpty()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            val config = compose.activity.resources.configuration
            assertEquals(Configuration.ORIENTATION_LANDSCAPE, config.orientation)
            assertTrue("Landscape width must be at least 600dp", config.screenWidthDp >= 600)
            assertTrue(config.screenWidthDp > config.screenHeightDp)
        }
        repeat(3) {
            for (i in 0..4) {
                compose.onNodeWithTag("nav-$i").performScrollTo().performClick()
                compose.waitForIdle()
            }
        }
        compose.onNodeWithTag("bsv-button").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("nav-0").assertDoesNotExist()
        compose.onNodeWithTag("bsv-button").performClick()
        compose.onNodeWithTag("nav-0").performScrollTo().assertIsDisplayed()
    }
}
