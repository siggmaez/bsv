package dev.bsv.app
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class NavigationTest {
 @get:Rule val compose = createAndroidComposeRule<MainActivity>()
 @Test fun allTabsAndFloatingMenuSurviveRapidNavigation() {
  compose.waitForIdle()
  repeat(3) { for(i in 0..4) {compose.onNodeWithTag("nav-$i").performScrollTo().performClick();compose.waitForIdle()} }
  compose.onNodeWithTag("bsv-button").performClick()
  compose.waitForIdle()
  compose.onNodeWithTag("nav-0").assertDoesNotExist()
  compose.onNodeWithTag("bsv-button").performClick()
  compose.onNodeWithTag("nav-0").performScrollTo().assertIsDisplayed()
 }
}
