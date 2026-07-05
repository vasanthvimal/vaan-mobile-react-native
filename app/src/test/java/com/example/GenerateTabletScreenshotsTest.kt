package com.example

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1080dp-h1920dp-mdpi", sdk = [36])
class GenerateTabletScreenshotsTest {

    @get:Rule val composeTestRule = createComposeRule()

    private fun captureScreenshot(fileName: String) {
        composeTestRule.waitForIdle()
        val path = "src/test/screenshots/$fileName"
        composeTestRule.onRoot().captureRoboImage(filePath = path)
        println("Enqueued screenshot capture for: $path")
    }

    @Test
    fun test_01_splash() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        captureScreenshot("tablet_10_01_splash.png")
    }

    @Test
    fun test_02_home() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        captureScreenshot("tablet_10_02_home.png")
    }

    @Test
    fun test_03_services() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.onNodeWithTag("nav_services").performClick()
        captureScreenshot("tablet_10_03_services.png")
    }

    @Test
    fun test_04_service_detail() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.onNodeWithTag("nav_services").performClick()
        composeTestRule.onNodeWithTag("service_card_cloud_transform").performClick()
        captureScreenshot("tablet_10_04_service_detail.png")
    }

    @Test
    fun test_05_insights() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.onNodeWithTag("nav_insights").performClick()
        captureScreenshot("tablet_10_05_insights.png")
    }

    @Test
    fun test_06_insight_detail() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.onNodeWithTag("nav_insights").performClick()
        composeTestRule.onNodeWithTag("article_card_fabric_analytics").performClick()
        captureScreenshot("tablet_10_06_insight_detail.png")
    }

    @Test
    fun test_07_chatbot() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.onNodeWithTag("nav_chatbot").performClick()
        captureScreenshot("tablet_10_07_chatbot.png")
    }

    @Test
    fun test_08_booking() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.onNodeWithTag("nav_bookings").performClick()
        captureScreenshot("tablet_10_08_booking.png")
    }
}

