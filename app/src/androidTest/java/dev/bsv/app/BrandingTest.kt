package dev.bsv.app

import android.graphics.drawable.AdaptiveIconDrawable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.bsv.settings.Settings
import dev.bsv.ui.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrandingTest {
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
    fun realLoadingComponentShowsBrandAndVersion() {
        compose.activityRule.scenario.onActivity { activity ->
            activity.setContent { BsvTheme(Settings()) { BsvSplash(animations = false) } }
        }
        compose.onNodeWithTag("bsv-splash").assertIsDisplayed()
        compose.onNodeWithTag("brand-logo").assertIsDisplayed()
        compose.onNodeWithTag("loading-indicator").assertIsDisplayed()
        compose.onNodeWithText("Brawl Stars Visuals").assertIsDisplayed()
        compose.onNodeWithText("Version 0.1.0 | Android").assertIsDisplayed()
    }

    @Test
    fun bannerPreservesWidescreenAspectRatio() {
        compose.activityRule.scenario.onActivity { activity ->
            activity.setContent { BsvTheme(Settings()) { BrandBanner(Modifier.width(400.dp)) } }
        }
        val bounds = compose.onNodeWithTag("brand-banner").getUnclippedBoundsInRoot()
        assertEquals(
            16f / 9f,
            (bounds.right - bounds.left).value / (bounds.bottom - bounds.top).value,
            .015f,
        )
    }

    @Test
    fun independentPackageUsesAdaptiveLauncherIcon() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        assertEquals("dev.bsv.visuals", context.packageName)
        val icon = context.packageManager.getApplicationIcon(context.packageName)
        assertTrue(icon is AdaptiveIconDrawable)
        assertTrue((icon as AdaptiveIconDrawable).foreground.intrinsicWidth > 0)
        assertTrue(icon.background.intrinsicHeight > 0)
    }
}
