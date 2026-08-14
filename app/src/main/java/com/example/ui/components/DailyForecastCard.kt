package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DailyForecast
import com.example.domain.model.TemperatureUnit

@Composable
fun DailyForecastCard(
    dailyItems: List<DailyForecast>,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    val minOfAll = dailyItems.minOfOrNull {
        if (tempUnit == TemperatureUnit.FAHRENHEIT) (it.minTempC * 9 / 5) + 32 else it.minTempC
    } ?: 10.0
    val maxOfAll = dailyItems.maxOfOrNull {
        if (tempUnit == TemperatureUnit.FAHRENHEIT) (it.maxTempC * 9 / 5) + 32 else it.maxTempC
    } ?: 30.0
    val totalRange = if (maxOfAll - minOfAll <= 0) 1.0 else maxOfAll - minOfAll

    GlassmorphicCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "7-DAY FORECAST",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Weekly Outlook",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            dailyItems.forEachIndexed { index, day ->
                val minT = if (tempUnit == TemperatureUnit.FAHRENHEIT) (day.minTempC * 9 / 5) + 32 else day.minTempC
                val maxT = if (tempUnit == TemperatureUnit.FAHRENHEIT) (day.maxTempC * 9 / 5) + 32 else day.maxTempC

                val startFraction = ((minT - minOfAll) / totalRange).toFloat().coerceIn(0f, 0.8f)
                val endFraction = ((maxT - minOfAll) / totalRange).toFloat().coerceIn(0.2f, 1f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day name
                    Text(
                        text = day.dayLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.width(72.dp)
                    )

                    // Weather Icon + Rain %
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(60.dp)
                    ) {
                        WeatherConditionIcon(
                            condition = day.condition,
                            isDay = true,
                            size = 22.dp
                        )
                        if (day.precipitationProb >= 20) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${day.precipitationProb}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF81D4FA),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Min Temp
                    Text(
                        text = "${minT.toInt()}°",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.width(32.dp)
                    )

                    // Range Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(endFraction)
                                .padding(start = (startFraction * 100).dp.coerceAtMost(60.dp))
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF64B5F6),
                                            Color(0xFFFFB74D),
                                            Color(0xFFFF8A65)
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Max Temp
                    Text(
                        text = "${maxT.toInt()}°",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.width(32.dp)
                    )
                }

                if (index < dailyItems.size - 1) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.1f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}
