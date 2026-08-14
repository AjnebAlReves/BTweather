package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currentTempC: Double,
    val feelsLikeTempC: Double,
    val minTempC: Double,
    val maxTempC: Double,
    val conditionName: String,
    val summaryText: String,
    val isDay: Boolean,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Int,
    val uvIndex: Double,
    val pressureHpa: Double,
    val airQualityIndex: Int,
    val sunriseTime: String,
    val sunsetTime: String,
    val hourlyJson: String, // serialized hourly items
    val dailyJson: String,  // serialized daily items
    val alertsJson: String = "", // serialized alerts
    val isGpsLocation: Boolean = false,
    val lastUpdatedMillis: Long,
    val providerUsed: String
)

@Entity(tableName = "clock_cities")
data class ClockCityEntity(
    @PrimaryKey
    val id: String,
    val cityName: String,
    val countryName: String,
    val timezoneId: String,
    val latitude: Double,
    val longitude: Double,
    val adminArea: String = "",
    val countryCode: String = "",
    val isPrimary: Boolean = false,
    val displayOrder: Int = 0,
    val lastCachedTemp: Double? = null,
    val lastCachedCondition: String? = null
)
