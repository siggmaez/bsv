package dev.bsv.app

import android.graphics.drawable.AdaptiveIconDrawable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
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
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @After
    fun releaseControlledFramesBeforeActivityClose() {
        // On API 26 a closing window can wait for a frame from the test-controlled
        // Compose clock. Remove animated content before ActivityScenario closes it.
        // The rule still verifies the full transition to DESTROYED.
        compose.runOnIdle {
            compose.activity.window.setWindowAnimations(0)
            compose.activity.setContent {}
        }
        compose.waitForIdle()
    }

    @Test
    fun realLoadingComponentShowsBrandAndVersion() {
        compose.setContent { BsvTheme(Settings()) { BsvSplash(animations = false) } }
        compose.onNodeWithTag("bsv-splash").assertIsDisplayed()
        compose.onNodeWithTag("brand-logo").assertIsDisplayed()
        compose.onNodeWithTag("loading-indicator").assertIsDisplayed()
        compose.onNodeWithText("Brawl Stars Visuals").assertIsDisplayed()
        compose.onNodeWithText("Version 0.1.0 | Android").assertIsDisplayed()
    }

    @Test
    fun bannerPreservesWidescreenAspectRatio() {
        compose.setContent { BsvTheme(Settings()) { BrandBanner(Modifier.width(400.dp)) } }
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
