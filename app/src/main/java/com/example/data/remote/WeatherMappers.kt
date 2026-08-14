package com.example.data.remote

import com.example.domain.model.WeatherCondition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object WeatherMappers {

    fun mapWmoCode(code: Int?, isDay: Boolean): WeatherCondition {
        return when (code) {
            0 -> if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
            1, 2 -> if (isDay) WeatherCondition.PARTLY_CLOUDY_DAY else WeatherCondition.PARTLY_CLOUDY_NIGHT
            3 -> WeatherCondition.CLOUDY
            45, 48 -> WeatherCondition.FOG
            51, 53, 55, 56, 57 -> WeatherCondition.DRIZZLE
            61, 63, 80 -> WeatherCondition.RAIN
            65, 66, 67, 81, 82 -> WeatherCondition.HEAVY_RAIN
            71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW
            95, 96, 99 -> WeatherCondition.THUNDERSTORM
            else -> if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
        }
    }

    fun mapMetNoSymbol(symbol: String?, isDay: Boolean): WeatherCondition {
        val sym = symbol?.lowercase(Locale.ROOT) ?: ""
        return when {
            sym.contains("thunder") -> WeatherCondition.THUNDERSTORM
            sym.contains("heavyrain") -> WeatherCondition.HEAVY_RAIN
            sym.contains("rain") || sym.contains("sleet") -> WeatherCondition.RAIN
            sym.contains("drizzle") -> WeatherCondition.DRIZZLE
            sym.contains("snow") -> WeatherCondition.SNOW
            sym.contains("fog") -> WeatherCondition.FOG
            sym.contains("cloudy") || sym.contains("overcast") -> WeatherCondition.CLOUDY
            sym.contains("partlycloudy") || sym.contains("fair") -> if (isDay) WeatherCondition.PARTLY_CLOUDY_DAY else WeatherCondition.PARTLY_CLOUDY_NIGHT
            sym.contains("clearsky") -> if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
            else -> if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT
        }
    }

    fun formatHourLabel(isoString: String): String {
        return try {
            val ldt = LocalDateTime.parse(isoString.substring(0, 16))
            val hour = ldt.hour
            when {
                hour == 0 -> "12 AM"
                hour < 12 -> "$hour AM"
                hour == 12 -> "12 PM"
                else -> "${hour - 12} PM"
            }
        } catch (e: Exception) {
            isoString.takeLast(5)
        }
    }

    fun formatDayLabel(isoString: String, index: Int): String {
        if (index == 0) return "Today"
        if (index == 1) return "Tomorrow"
        return try {
            val date = java.time.LocalDate.parse(isoString.take(10))
            date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
        } catch (e: Exception) {
            "Day $index"
        }
    }
}
