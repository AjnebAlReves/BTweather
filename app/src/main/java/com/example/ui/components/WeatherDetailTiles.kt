package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PressureUnit
import com.example.domain.model.WeatherData
import com.example.domain.model.WindSpeedUnit

@Composable
fun WeatherDetailGrid(
    weather: WeatherData,
    windUnit: WindSpeedUnit,
    pressureUnit: PressureUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Row 1: UV Index & Humidity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // UV Index
            MetricTile(
                title = "UV INDEX",
                icon = Icons.Filled.WbSunny,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = "${weather.uvIndex.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = when {
                            weather.uvIndex <= 2 -> "Low"
                            weather.uvIndex <= 5 -> "Moderate"
                            weather.uvIndex <= 7 -> "High"
                            weather.uvIndex <= 10 -> "Very High"
                            else -> "Extreme"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    UvArcGauge(uvIndex = weather.uvIndex)
                }
            }

            // Humidity
            MetricTile(
                title = "HUMIDITY",
                icon = Icons.Filled.WaterDrop,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = "${weather.humidityPercent}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Dew point: ${(weather.currentTempC - ((100 - weather.humidityPercent) / 5)).toInt()}°",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (weather.humidityPercent > 65) "Humid conditions" else "Comfortable level",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Row 2: Wind & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Wind
            val displayWind = when (windUnit) {
                WindSpeedUnit.MPH -> "${(weather.windSpeedKmh * 0.621371).toInt()} mph"
                WindSpeedUnit.M_S -> "${(weather.windSpeedKmh / 3.6).toInt()} m/s"
                WindSpeedUnit.KM_H -> "${weather.windSpeedKmh.toInt()} km/h"
            }
            MetricTile(
                title = "WIND",
                icon = Icons.Filled.Air,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = displayWind,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Direction: ${weather.windDirectionDegrees}°",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Gentle breeze",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Air Pressure / Quality
            val displayPressure = when (pressureUnit) {
                PressureUnit.IN_HG -> "%.2f inHg".format(weather.pressureHpa * 0.02953)
                PressureUnit.HPA -> "${weather.pressureHpa.toInt()} hPa"
            }
            MetricTile(
                title = "AIR PRESSURE",
                icon = Icons.Filled.Compress,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = displayPressure,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Stable barometer",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Eco,
                            contentDescription = "AQI",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AQI: ${weather.airQualityIndex} (${weather.airQualityLabel})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricTile(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassmorphicCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 0.8.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun UvArcGauge(uvIndex: Double) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
    ) {
        val strokeWidth = 4.dp.toPx()
        val totalW = size.width
        val fraction = (uvIndex / 11.0).toFloat().coerceIn(0f, 1f)

        // Background track
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(0f, size.height / 2),
            end = Offset(totalW, size.height / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Progress line
        drawLine(
            color = when {
                uvIndex <= 2 -> Color(0xFF81C784)
                uvIndex <= 5 -> Color(0xFFFFD54F)
                uvIndex <= 7 -> Color(0xFFFFB74D)
                uvIndex <= 10 -> Color(0xFFE57373)
                else -> Color(0xFFBA68C8)
            },
            start = Offset(0f, size.height / 2),
            end = Offset(totalW * fraction, size.height / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
