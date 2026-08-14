package com.example.data.repository

import com.example.data.local.datastore.UserSettingsDataStore
import com.example.domain.model.PressureUnit
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.TimeFormatPreference
import com.example.domain.model.UserSettings
import com.example.domain.model.WeatherProviderType
import com.example.domain.model.WindSpeedUnit
import com.example.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserSettingsRepositoryImpl(
    private val dataStore: UserSettingsDataStore
) : UserSettingsRepository {

    override fun getSettings(): Flow<UserSettings> = dataStore.userSettingsFlow

    override suspend fun updateSettings(settings: UserSettings) {
        dataStore.saveSettings(settings)
    }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        val current = dataStore.userSettingsFlow.first()
        dataStore.saveSettings(current.copy(temperatureUnit = unit))
    }

    override suspend fun updateTimeFormat(format: TimeFormatPreference) {
        val current = dataStore.userSettingsFlow.first()
        dataStore.saveSettings(current.copy(timeFormat = format))
    }

    override suspend fun updateProvider(provider: WeatherProviderType, key: String) {
        val current = dataStore.userSettingsFlow.first()
        dataStore.saveSettings(current.copy(providerType = provider, customApiKey = key))
    }
}
