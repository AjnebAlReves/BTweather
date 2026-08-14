package com.example.domain.repository

import com.example.domain.model.WeatherData
import com.example.domain.model.WorldClockItem
import com.example.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        cityName: String,
        countryName: String,
        forceRefresh: Boolean = false
    ): Result<WeatherData>

    fun getCachedWeatherData(cityName: String): Flow<WeatherData?>
    suspend fun searchLocations(query: String): Result<List<WorldClockItem>>
}

interface WorldClockRepository {
    fun getSavedClockLocations(): Flow<List<WorldClockItem>>
    suspend fun addClockLocation(item: WorldClockItem)
    suspend fun removeClockLocation(id: String)
    suspend fun refreshClockTimes(): List<WorldClockItem>
}

interface UserSettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun updateTemperatureUnit(unit: com.example.domain.model.TemperatureUnit)
    suspend fun updateTimeFormat(format: com.example.domain.model.TimeFormatPreference)
    suspend fun updateProvider(provider: com.example.domain.model.WeatherProviderType, key: String)
    fun getLastSelectedCity(): Flow<com.example.domain.model.WorldClockItem?>
    suspend fun saveLastSelectedCity(city: com.example.domain.model.WorldClockItem)
}
