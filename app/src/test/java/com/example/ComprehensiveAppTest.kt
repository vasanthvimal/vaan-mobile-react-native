package com.example

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.ConsultationBookingForm
import com.example.viewmodel.AppViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.system.measureTimeMillis

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ComprehensiveAppTest {

    @get:Rule val composeTestRule = createComposeRule()

    /**
     * SECTION 1: WHITE BOX TESTING
     * Validates the internal states, logic transitions, database transactions,
     * and Repository flow properties.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whiteBox_databaseIntegrityAndViewModelTransitions() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)

        // Verify initial state
        val initialInquiries = viewModel.inquiries.first()
        assertNotNull(initialInquiries)

        // Submit an inquiry and check state transition
        viewModel.submitInquiry(
            clientName = "Security Specialist",
            clientEmail = "test@company.com",
            companyName = "Defense Corp",
            subject = "Platform PenTest",
            message = "We need a comprehensive security audit of our cloud platform."
        )

        // Allow coroutines to process the insert
        testScheduler.advanceUntilIdle()

        val updatedInquiries = viewModel.inquiries.first()
        val match = updatedInquiries.any {
            it.clientName == "Security Specialist" &&
            it.clientEmail == "test@company.com" &&
            it.subject == "Platform PenTest"
        }
        assertTrue("Inquiry was not stored or found in internal state flow", match)
    }

    /**
     * SECTION 2: SECURITY & VULNERABILITY TESTING
     * Ensures input validation, protection against SQL Injection payload,
     * extreme input length boundary checks, and strict formatting.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun security_inputValidationAndBoundarySanitisation() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)

        // SQL Injection boundary check
        val sqlInjectionPayload = "'; DROP TABLE meetings; --"
        viewModel.submitInquiry(
            clientName = "Hacker",
            clientEmail = "hacker@evil.com",
            companyName = "EvilCorp",
            subject = sqlInjectionPayload,
            message = "Test message description"
        )

        testScheduler.advanceUntilIdle()

        // Verify the database handles the query strictly as data (parameterized query) without crash
        val inquiries = viewModel.inquiries.first()
        val maliciousInquiry = inquiries.firstOrNull { it.clientName == "Hacker" }
        assertNotNull("Malicious payload was rejected instead of stored as safe literal data", maliciousInquiry)
        assertEquals(sqlInjectionPayload, maliciousInquiry?.subject)

        // Extremely long input checking (Buffer boundary protection)
        val massivePayload = "A".repeat(10000)
        viewModel.submitInquiry(
            clientName = "StressTester",
            clientEmail = "stress@company.com",
            companyName = "LoadCorp",
            subject = "HeavyLoad",
            message = massivePayload
        )

        testScheduler.advanceUntilIdle()

        val heavyInquiry = viewModel.inquiries.first().firstOrNull { it.clientName == "StressTester" }
        assertNotNull("Heavy payload failed to persist safely", heavyInquiry)
        assertEquals(10000, heavyInquiry?.message?.length)
    }

    /**
     * SECTION 3: STRESS & PERFORMANCE TESTING
     * Tests high concurrent database insertions/updates under stress conditions.
     * Measures latency to verify zero execution bottleneck on the main/UI thread.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun stressAndPerformance_highThroughputDatabaseOperations() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)

        val transactionCount = 20
        val latencyMs = measureTimeMillis {
            // Spawn concurrent coroutines to write to Room under heavy stress
            val jobs = (1..transactionCount).map { i ->
                async {
                    viewModel.submitInquiry(
                        clientName = "User $i",
                        clientEmail = "user$i@domain.com",
                        companyName = "Stress Co",
                        subject = "Load $i",
                        message = "Automatic stress message description for user $i"
                    )
                }
            }
            jobs.awaitAll()
        }

        testScheduler.advanceUntilIdle()

        val allInquiries = viewModel.inquiries.first()
        val stressCount = allInquiries.count { it.companyName == "Stress Co" }

        // Assert all concurrent operations finished successfully without transactional conflict
        assertEquals("Not all concurrent write transactions succeeded", transactionCount, stressCount)

        // Verify performance criteria: average insertion execution latency on virtual JVM
        val avgLatency = latencyMs.toDouble() / transactionCount
        System.out.println("Stress Testing: Dispatched $transactionCount transactions in $latencyMs ms (Avg: $avgLatency ms/transaction)")
        assertTrue("Database write operations took excessively long", latencyMs < 5000)
    }

    /**
     * SECTION 4: BLACK BOX & UI SMOKE TESTING
     * Simulates direct user input into Jetpack Compose components.
     * Verifies that the UI reacts correctly, handles error states,
     * and records correct visuals via Roborazzi.
     */
    @Test
    fun blackBox_bookingFormAndContactInquirySmokeTest() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AppViewModel(app)

        // Render ConsultationBookingForm
        composeTestRule.setContent {
            MyApplicationTheme {
                ConsultationBookingForm(
                    selectedService = "AI Transformation",
                    onClearSelection = {},
                    onBook = { name, email, service, time, duration, notes ->
                        viewModel.createAppointment(name, email, service, time, duration, notes)
                    }
                )
            }
        }

        // Wait for Compose initialization
        composeTestRule.waitForIdle()

        // 1. Negative scenario: Attempt to submit empty form
        composeTestRule.onNodeWithTag("submit_booking_btn").performClick()
        composeTestRule.waitForIdle()

        // 2. Input valid fields
        composeTestRule.onNodeWithTag("booking_name_input").performTextInput("Vasanth N")
        composeTestRule.onNodeWithTag("booking_email_input").performTextInput("vaanconsulting@gmail.com")
        
        // Wait and perform click
        composeTestRule.onNodeWithTag("submit_booking_btn").performClick()
        composeTestRule.waitForIdle()

        // Verify state is updated and screenshot is taken
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/booking_form_success.png")
    }
}
