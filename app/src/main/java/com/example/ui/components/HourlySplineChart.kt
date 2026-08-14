package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.HourlyForecast
import com.example.domain.model.TemperatureUnit

@Composable
fun HourlySplineChart(
    hourlyItems: List<HourlyForecast>,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    if (hourlyItems.isEmpty()) return

    val scrollState = rememberScrollState()
    val itemWidth = 68.dp
    val totalWidth = itemWidth * hourlyItems.size

    val temps = hourlyItems.map {
        if (tempUnit == TemperatureUnit.FAHRENHEIT) (it.tempC * 9 / 5) + 32 else it.tempC
    }
    val minTemp = temps.minOrNull() ?: 15.0
    val maxTemp = temps.maxOrNull() ?: 25.0
    val range = if (maxTemp - minTemp < 1.0) 1.0 else maxTemp - minTemp

    GlassmorphicCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HOURLY FORECAST",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "24-Hour Trends",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                // Background Canvas for the smooth temperature line and dots
                Canvas(
                    modifier = Modifier
                        .width(totalWidth)
                        .height(170.dp)
                ) {
                    val wPx = itemWidth.toPx()
                    val chartTop = 80.dp.toPx()
                    val chartHeight = 44.dp.toPx()

                    val points = hourlyItems.mapIndexed { idx, item ->
                        val t = if (tempUnit == TemperatureUnit.FAHRENHEIT) (item.tempC * 9 / 5) + 32 else item.tempC
                        val norm = ((t - minTemp) / range).toFloat().coerceIn(0f, 1f)
                        val x = idx * wPx + (wPx / 2)
                        val y = chartTop + chartHeight * (1f - norm)
                        Offset(x, y)
                    }

                    if (points.size > 1) {
                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val controlX = (p0.x + p1.x) / 2
                                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            }
                        }

                        // Glow line
                        drawPath(
                            path = path,
                            color = Color(0xFFF9D08B).copy(alpha = 0.4f),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Main line
                        drawPath(
                            path = path,
                            color = Color(0xFFFDF0D5),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Points
                        points.forEach { pt ->
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color(0xFFF59E0B),
                                radius = 2.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // Row of column items placed precisely above and below the line
                Row(
                    modifier = Modifier.width(totalWidth)
                ) {
                    hourlyItems.forEachIndexed { idx, item ->
                        val displayTemp = if (tempUnit == TemperatureUnit.FAHRENHEIT) {
                            "${((item.tempC * 9 / 5) + 32).toInt()}°"
                        } else {
                            "${item.tempC.toInt()}°"
                        }

                        Column(
                            modifier = Modifier
                                .width(itemWidth)
                                .height(170.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Hour Label
                            Text(
                                text = item.hourLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )

                            // Weather Icon
                            WeatherConditionIcon(
                                condition = item.condition,
                                isDay = item.isDay,
                                size = 24.dp
                            )

                            // Temperature label
                            Text(
                                text = displayTemp,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            // Precipitation %
                            if (item.precipitationProb > 0) {
                                Text(
                                    text = "${item.precipitationProb}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF81D4FA),
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "-",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
