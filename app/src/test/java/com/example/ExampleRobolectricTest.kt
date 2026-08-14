package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.AlertSeverity
import com.example.ui.screens.search.countryCodeToEmoji
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Weather & Clock", appName)
    }

    @Test
    fun `countryCodeToEmoji returns correct flags`() {
        val usFlag = countryCodeToEmoji("US")
        val esFlag = countryCodeToEmoji("ES")
        assertTrue(usFlag.isNotEmpty())
        assertTrue(esFlag.isNotEmpty())
    }

    @Test
    fun `alert severity enum mappings`() {
        assertEquals("Extreme Warning", AlertSeverity.EXTREME.label)
        assertEquals("Severe Warning", AlertSeverity.WARNING.label)
        assertEquals("Watch", AlertSeverity.WATCH.label)
        assertEquals("Advisory", AlertSeverity.ADVISORY.label)
    }
}
