package com.example.domain.model

enum class WeatherCondition(val label: String) {
    CLEAR_DAY("Clear"),
    CLEAR_NIGHT("Clear"),
    PARTLY_CLOUDY_DAY("Partly Cloudy"),
    PARTLY_CLOUDY_NIGHT("Partly Cloudy"),
    CLOUDY("Overcast"),
    FOG("Foggy"),
    DRIZZLE("Light Drizzle"),
    RAIN("Rain Showers"),
    HEAVY_RAIN("Heavy Rain"),
    THUNDERSTORM("Thunderstorms"),
    SNOW("Snow Showers"),
    WINDY("Windy & Gusty")
}

enum class AlertSeverity(val label: String, val level: Int) {
    ADVISORY("Advisory", 1),
    WATCH("Watch", 2),
    WARNING("Severe Warning", 3),
    EXTREME("Extreme Warning", 4)
}

data class SevereWeatherAlert(
    val id: String,
    val locationName: String,
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val effectiveTime: String,
    val expiresTime: String,
    val sender: String = "National Weather Service",
    val condition: WeatherCondition = WeatherCondition.THUNDERSTORM
)

data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null,
    val provider: String = "GPS"
)

data class HourlyForecast(
    val timeIso: String,
    val hourLabel: String,
    val tempC: Double,
    val condition: WeatherCondition,
    val precipitationProb: Int,
    val isDay: Boolean,
    val isSunrise: Boolean = false,
    val isSunset: Boolean = false
)

data class DailyForecast(
    val dateIso: String,
    val dayLabel: String,
    val minTempC: Double,
    val maxTempC: Double,
    val condition: WeatherCondition,
    val precipitationProb: Int,
    val description: String
)

data class WeatherData(
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currentTempC: Double,
    val feelsLikeTempC: Double,
    val minTempC: Double,
    val maxTempC: Double,
    val condition: WeatherCondition,
    val summaryText: String,
    val isDay: Boolean,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Int = 180,
    val uvIndex: Double,
    val pressureHpa: Double,
    val visibilityKm: Double = 10.0,
    val airQualityIndex: Int = 28, // Good AQI
    val airQualityLabel: String = "Good",
    val sunriseTime: String = "06:15 AM",
    val sunsetTime: String = "08:30 PM",
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList(),
    val alerts: List<SevereWeatherAlert> = emptyList(),
    val isGpsLocation: Boolean = false,
    val lastUpdatedMillis: Long = System.currentTimeMillis(),
    val providerUsed: String = "Open-Meteo"
)

data class WorldClockItem(
    val id: String,
    val cityName: String,
    val countryName: String,
    val timezoneId: String,
    val latitude: Double,
    val longitude: Double,
    val adminArea: String = "",
    val countryCode: String = "",
    val formattedTime: String = "",
    val formattedAmPm: String = "",
    val formattedDate: String = "",
    val timeDiffHours: Double = 0.0,
    val isDay: Boolean = true,
    val tempC: Double? = null,
    val condition: WeatherCondition? = null,
    val isGpsLocation: Boolean = false
)

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

enum class TimeFormatPreference(val label: String) {
    FORMAT_12H("12-hour (AM/PM)"),
    FORMAT_24H("24-hour")
}

enum class WindSpeedUnit(val symbol: String) {
    KM_H("km/h"),
    MPH("mph"),
    M_S("m/s")
}

enum class PressureUnit(val symbol: String) {
    HPA("hPa"),
    IN_HG("inHg")
}

enum class WeatherProviderType(val displayName: String, val requiresKey: Boolean) {
    AUTO_FALLBACK("Auto Fallback (Open-Meteo + Met.no)", false),
    OPEN_METEO("Open-Meteo API", false),
    MET_NO("Met.no (Yr.no)", false),
    OPEN_WEATHER_MAP("OpenWeatherMap", true),
    WEATHER_API("WeatherAPI.com", true)
}

data class UserSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val timeFormat: TimeFormatPreference = TimeFormatPreference.FORMAT_12H,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KM_H,
    val pressureUnit: PressureUnit = PressureUnit.HPA,
    val providerType: WeatherProviderType = WeatherProviderType.AUTO_FALLBACK,
    val customApiKey: String = "",
    val updateIntervalMinutes: Int = 60,
    val dynamicThemeEnabled: Boolean = true,
    val glassmorphismEffect: Boolean = true,
    val severeWeatherAlertsEnabled: Boolean = true,
    val notificationSoundEnabled: Boolean = true,
    val autoLocateOnLaunch: Boolean = true
)
