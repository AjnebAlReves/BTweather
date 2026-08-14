package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.WeatherCondition

@Composable
fun WeatherConditionIcon(
    condition: WeatherCondition,
    isDay: Boolean = true,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    tint: Color? = null
) {
    val (icon, defaultTint) = when (condition) {
        WeatherCondition.CLEAR_DAY -> Icons.Filled.WbSunny to Color(0xFFFFB300)
        WeatherCondition.CLEAR_NIGHT -> Icons.Filled.NightsStay to Color(0xFFFDD835)
        WeatherCondition.PARTLY_CLOUDY_DAY -> Icons.Filled.WbCloudy to Color(0xFFFFD54F)
        WeatherCondition.PARTLY_CLOUDY_NIGHT -> Icons.Filled.DarkMode to Color(0xFFB0BEC5)
        WeatherCondition.CLOUDY -> Icons.Filled.Cloud to Color(0xFFECEFF1)
        WeatherCondition.FOG -> Icons.Filled.Grain to Color(0xFFCFD8DC)
        WeatherCondition.DRIZZLE -> Icons.Filled.WaterDrop to Color(0xFF81D4FA)
        WeatherCondition.RAIN -> Icons.Filled.WaterDrop to Color(0xFF29B6F6)
        WeatherCondition.HEAVY_RAIN -> Icons.Filled.WaterDrop to Color(0xFF0288D1)
        WeatherCondition.THUNDERSTORM -> Icons.Filled.Thunderstorm to Color(0xFFFFCA28)
        WeatherCondition.SNOW -> Icons.Filled.Grain to Color(0xFFE1F5FE)
        WeatherCondition.WINDY -> Icons.Filled.Air to Color(0xFFB0BEC5)
    }

    Icon(
        imageVector = icon,
        contentDescription = condition.label,
        tint = tint ?: defaultTint,
        modifier = modifier.size(size)
    )
}
