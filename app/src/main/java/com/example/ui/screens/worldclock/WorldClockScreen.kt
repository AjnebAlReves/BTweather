package com.example.ui.screens.worldclock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.WorldClockItem
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onNavigateToSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeOffsetHours by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "World Clock",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onNavigateToSearch) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add City"
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            // Interactive Time Scrubber / Timezone comparison
            item {
                Spacer(modifier = Modifier.height(10.dp))
                TimeScrubberCard(
                    offsetHours = timeOffsetHours,
                    onOffsetChanged = { timeOffsetHours = it },
                    onReset = { timeOffsetHours = 0f }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Analog Clock Preview for Local / Primary City
            item {
                uiState.savedCities.firstOrNull()?.let { primary ->
                    PrimaryAnalogClockHeader(
                        city = primary,
                        offsetHours = timeOffsetHours
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // List of world cities
            item {
                Text(
                    text = "ALL CITIES (${uiState.savedCities.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.savedCities) { city ->
                WorldClockListItem(
                    city = city,
                    offsetHours = timeOffsetHours,
                    onDelete = { viewModel.removeCity(city.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TimeScrubberCard(
    offsetHours: Float,
    onOffsetChanged: (Float) -> Unit,
    onReset: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Timezone Comparison Slider",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (offsetHours != 0f) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val offsetText = if (offsetHours == 0f) {
                "Showing current real-time across all cities"
            } else {
                val sign = if (offsetHours > 0) "+" else ""
                "Simulating ${sign}%.1f hours from now".format(offsetHours)
            }

            Text(
                text = offsetText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = offsetHours,
                onValueChange = onOffsetChanged,
                valueRange = -12f..12f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun PrimaryAnalogClockHeader(
    city: WorldClockItem,
    offsetHours: Float
) {
    val zdt = calculateOffsetTime(city.timezoneId, offsetHours)
    val hour = zdt.hour
    val minute = zdt.minute
    val second = zdt.second
    val isDay = hour in 6..18

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = if (isDay) Color(0xFFFFFBEB) else Color(0xFF1E1E2E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.cityName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDay) Color(0xFF78350F) else Color(0xFFE2E8F0)
                )
                Text(
                    text = "${city.countryName} (${city.timezoneId})",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDay) Color(0xFF92400E) else Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = zdt.format(DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.getDefault())),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDay) Color(0xFFD97706) else Color(0xFF818CF8)
                )
            }

            // Analog Clock Canvas
            AnalogClockView(
                hour = hour,
                minute = minute,
                second = second,
                isDay = isDay,
                modifier = Modifier.size(90.dp)
            )
        }
    }
}

@Composable
fun AnalogClockView(
    hour: Int,
    minute: Int,
    second: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        // Outer dial circle
        drawCircle(
            color = if (isDay) Color(0xFFFDE68A) else Color(0xFF312E81),
            radius = radius,
            center = center
        )
        drawCircle(
            color = if (isDay) Color(0xFFFFFFFF) else Color(0xFF1E1B4B),
            radius = radius - 3.dp.toPx(),
            center = center
        )

        // Hour hand
        val hourAngle = Math.toRadians(((hour % 12 + minute / 60f) * 30.0) - 90.0)
        val hourHandLength = radius * 0.5f
        val hourEnd = Offset(
            center.x + (hourHandLength * cos(hourAngle)).toFloat(),
            center.y + (hourHandLength * sin(hourAngle)).toFloat()
        )
        drawLine(
            color = if (isDay) Color(0xFF78350F) else Color(0xFFE0E7FF),
            start = center,
            end = hourEnd,
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Minute hand
        val minuteAngle = Math.toRadians((minute * 6.0) - 90.0)
        val minuteHandLength = radius * 0.72f
        val minuteEnd = Offset(
            center.x + (minuteHandLength * cos(minuteAngle)).toFloat(),
            center.y + (minuteHandLength * sin(minuteAngle)).toFloat()
        )
        drawLine(
            color = if (isDay) Color(0xFFD97706) else Color(0xFF818CF8),
            start = center,
            end = minuteEnd,
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center pin
        drawCircle(
            color = if (isDay) Color(0xFFD97706) else Color(0xFF818CF8),
            radius = 3.dp.toPx(),
            center = center
        )
    }
}

@Composable
fun WorldClockListItem(
    city: WorldClockItem,
    offsetHours: Float,
    onDelete: () -> Unit
) {
    val zdt = calculateOffsetTime(city.timezoneId, offsetHours)
    val hour = zdt.hour
    val isDay = hour in 6..18
    val formattedTime = zdt.format(DateTimeFormatter.ofPattern("h:mm", Locale.getDefault()))
    val amPm = zdt.format(DateTimeFormatter.ofPattern("a", Locale.getDefault()))
    val formattedDate = zdt.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault()))

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Day/Night indicator circle
                Surface(
                    shape = CircleShape,
                    color = if (isDay) Color(0xFFFEF3C7) else Color(0xFF312E81),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isDay) Icons.Filled.WbSunny else Icons.Filled.DarkMode,
                            contentDescription = if (isDay) "Day" else "Night",
                            tint = if (isDay) Color(0xFFD97706) else Color(0xFF818CF8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = city.cityName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$formattedDate • ${city.countryName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Time digits & Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = amPm,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    val diffSign = if (city.timeDiffHours >= 0) "+" else ""
                    val hrs = if (city.timeDiffHours % 1.0 == 0.0) "${city.timeDiffHours.toInt()}" else "%.1f".format(city.timeDiffHours)
                    Text(
                        text = "$diffSign$hrs hrs from local",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun calculateOffsetTime(timezoneId: String, offsetHours: Float): ZonedDateTime {
    return try {
        val zone = ZoneId.of(timezoneId)
        val now = ZonedDateTime.now(zone)
        val minutesToAdd = (offsetHours * 60).toLong()
        now.plusMinutes(minutesToAdd)
    } catch (e: Exception) {
        ZonedDateTime.now()
    }
}
