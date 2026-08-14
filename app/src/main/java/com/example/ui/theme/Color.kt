package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.domain.model.WeatherCondition

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// One UI / Weather Custom Palette
val OneUiBlue = Color(0xFF1E88E5)
val OneUiDarkBlue = Color(0xFF0D47A1)
val OneUiCardDark = Color(0xFF1E1E24)
val OneUiCardLight = Color(0xFFFFFFFF)
val OneUiAmber = Color(0xFFF59E0B)
val OneUiOrange = Color(0xFFEA580C)
val OneUiTeal = Color(0xFF0D9488)
val OneUiPurple = Color(0xFF8B5CF6)

object WeatherGradients {
    val ClearDay = listOf(
        Color(0xFF4A7AB5),
        Color(0xFF6894C7),
        Color(0xFFE29F68),
        Color(0xFFF3BD7B)
    )

    val ClearNight = listOf(
        Color(0xFF0B132B),
        Color(0xFF1C2541),
        Color(0xFF3A506B),
        Color(0xFF4A4E69)
    )

    val PartlyCloudyDay = listOf(
        Color(0xFF43658B),
        Color(0xFF628395),
        Color(0xFFDDA77B),
        Color(0xFFEDC988)
    )

    val PartlyCloudyNight = listOf(
        Color(0xFF111D28),
        Color(0xFF1E2F40),
        Color(0xFF2E4057),
        Color(0xFF3F4B5E)
    )

    val Rainy = listOf(
        Color(0xFF2C3E50),
        Color(0xFF34495E),
        Color(0xFF4A6572),
        Color(0xFF5D6D7E)
    )

    val Thunderstorm = listOf(
        Color(0xFF1A1A24),
        Color(0xFF252A34),
        Color(0xFF393E46),
        Color(0xFF4E586E)
    )

    val Snowy = listOf(
        Color(0xFF3A6073),
        Color(0xFF4F7A94),
        Color(0xFF89A9C2),
        Color(0xFFCBDCE8)
    )

    val Foggy = listOf(
        Color(0xFF4F5D65),
        Color(0xFF6B7B83),
        Color(0xFF8F9DA4),
        Color(0xFFB5C0C6)
    )

    fun getGradientForCondition(condition: WeatherCondition, isDay: Boolean): List<Color> {
        return when (condition) {
            WeatherCondition.CLEAR_DAY -> ClearDay
            WeatherCondition.CLEAR_NIGHT -> ClearNight
            WeatherCondition.PARTLY_CLOUDY_DAY -> PartlyCloudyDay
            WeatherCondition.PARTLY_CLOUDY_NIGHT -> PartlyCloudyNight
            WeatherCondition.CLOUDY -> if (isDay) PartlyCloudyDay else PartlyCloudyNight
            WeatherCondition.RAIN, WeatherCondition.DRIZZLE, WeatherCondition.HEAVY_RAIN -> Rainy
            WeatherCondition.THUNDERSTORM -> Thunderstorm
            WeatherCondition.SNOW -> Snowy
            WeatherCondition.FOG -> Foggy
            WeatherCondition.WINDY -> if (isDay) ClearDay else ClearNight
        }
    }
}
