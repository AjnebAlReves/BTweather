package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.PressureUnit
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.TimeFormatPreference
import com.example.domain.model.UserSettings
import com.example.domain.model.WeatherProviderType
import com.example.domain.model.WindSpeedUnit
import com.example.domain.model.WorldClockItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings_pref")

class UserSettingsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val PRESSURE_UNIT = stringPreferencesKey("pressure_unit")
        val PROVIDER_TYPE = stringPreferencesKey("provider_type")
        val CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")
        val UPDATE_INTERVAL = intPreferencesKey("update_interval")
        val DYNAMIC_THEME = booleanPreferencesKey("dynamic_theme")
        val GLASSMORPHISM = booleanPreferencesKey("glassmorphism")
        val SEVERE_ALERTS = booleanPreferencesKey("severe_alerts")
        val NOTIF_SOUND = booleanPreferencesKey("notif_sound")
        val AUTO_LOCATE = booleanPreferencesKey("auto_locate")
        val SELECTED_CITY_ID = stringPreferencesKey("selected_city_id")
        val SELECTED_CITY_NAME = stringPreferencesKey("selected_city_name")
        val SELECTED_CITY_COUNTRY = stringPreferencesKey("selected_city_country")
        val SELECTED_CITY_TIMEZONE = stringPreferencesKey("selected_city_timezone")
        val SELECTED_CITY_LAT = doublePreferencesKey("selected_city_lat")
        val SELECTED_CITY_LON = doublePreferencesKey("selected_city_lon")
        val SELECTED_CITY_IS_GPS = booleanPreferencesKey("selected_city_is_gps")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { pref ->
        val tempUnit = when (pref[PreferencesKeys.TEMP_UNIT]) {
            TemperatureUnit.FAHRENHEIT.name -> TemperatureUnit.FAHRENHEIT
            else -> TemperatureUnit.CELSIUS
        }
        val timeFormat = when (pref[PreferencesKeys.TIME_FORMAT]) {
            TimeFormatPreference.FORMAT_24H.name -> TimeFormatPreference.FORMAT_24H
            else -> TimeFormatPreference.FORMAT_12H
        }
        val windUnit = when (pref[PreferencesKeys.WIND_UNIT]) {
            WindSpeedUnit.MPH.name -> WindSpeedUnit.MPH
            WindSpeedUnit.M_S.name -> WindSpeedUnit.M_S
            else -> WindSpeedUnit.KM_H
        }
        val pressureUnit = when (pref[PreferencesKeys.PRESSURE_UNIT]) {
            PressureUnit.IN_HG.name -> PressureUnit.IN_HG
            else -> PressureUnit.HPA
        }
        val providerType = when (pref[PreferencesKeys.PROVIDER_TYPE]) {
            WeatherProviderType.OPEN_METEO.name -> WeatherProviderType.OPEN_METEO
            WeatherProviderType.MET_NO.name -> WeatherProviderType.MET_NO
            WeatherProviderType.OPEN_WEATHER_MAP.name -> WeatherProviderType.OPEN_WEATHER_MAP
            WeatherProviderType.WEATHER_API.name -> WeatherProviderType.WEATHER_API
            else -> WeatherProviderType.AUTO_FALLBACK
        }
        val customApiKey = pref[PreferencesKeys.CUSTOM_API_KEY] ?: ""
        val updateInterval = pref[PreferencesKeys.UPDATE_INTERVAL] ?: 60
        val dynamicTheme = pref[PreferencesKeys.DYNAMIC_THEME] ?: true
        val glassmorphism = pref[PreferencesKeys.GLASSMORPHISM] ?: true
        val severeAlerts = pref[PreferencesKeys.SEVERE_ALERTS] ?: true
        val notifSound = pref[PreferencesKeys.NOTIF_SOUND] ?: true
        val autoLocate = pref[PreferencesKeys.AUTO_LOCATE] ?: true

        UserSettings(
            temperatureUnit = tempUnit,
            timeFormat = timeFormat,
            windSpeedUnit = windUnit,
            pressureUnit = pressureUnit,
            providerType = providerType,
            customApiKey = customApiKey,
            updateIntervalMinutes = updateInterval,
            dynamicThemeEnabled = dynamicTheme,
            glassmorphismEffect = glassmorphism,
            severeWeatherAlertsEnabled = severeAlerts,
            notificationSoundEnabled = notifSound,
            autoLocateOnLaunch = autoLocate
        )
    }

    val lastSelectedCityFlow: Flow<WorldClockItem?> = context.dataStore.data.map { pref ->
        val id = pref[PreferencesKeys.SELECTED_CITY_ID]
        val lat = pref[PreferencesKeys.SELECTED_CITY_LAT]
        val lon = pref[PreferencesKeys.SELECTED_CITY_LON]
        if (id == null || lat == null || lon == null) return@map null
        WorldClockItem(
            id = id,
            cityName = pref[PreferencesKeys.SELECTED_CITY_NAME] ?: id,
            countryName = pref[PreferencesKeys.SELECTED_CITY_COUNTRY] ?: "",
            timezoneId = pref[PreferencesKeys.SELECTED_CITY_TIMEZONE] ?: "UTC",
            latitude = lat,
            longitude = lon,
            isGpsLocation = pref[PreferencesKeys.SELECTED_CITY_IS_GPS] ?: false
        )
    }

    suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.TEMP_UNIT] = settings.temperatureUnit.name
            pref[PreferencesKeys.TIME_FORMAT] = settings.timeFormat.name
            pref[PreferencesKeys.WIND_UNIT] = settings.windSpeedUnit.name
            pref[PreferencesKeys.PRESSURE_UNIT] = settings.pressureUnit.name
            pref[PreferencesKeys.PROVIDER_TYPE] = settings.providerType.name
            pref[PreferencesKeys.CUSTOM_API_KEY] = settings.customApiKey
            pref[PreferencesKeys.UPDATE_INTERVAL] = settings.updateIntervalMinutes
            pref[PreferencesKeys.DYNAMIC_THEME] = settings.dynamicThemeEnabled
            pref[PreferencesKeys.GLASSMORPHISM] = settings.glassmorphismEffect
            pref[PreferencesKeys.SEVERE_ALERTS] = settings.severeWeatherAlertsEnabled
            pref[PreferencesKeys.NOTIF_SOUND] = settings.notificationSoundEnabled
            pref[PreferencesKeys.AUTO_LOCATE] = settings.autoLocateOnLaunch
        }
    }

    suspend fun saveLastSelectedCity(city: WorldClockItem) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.SELECTED_CITY_ID] = city.id
            pref[PreferencesKeys.SELECTED_CITY_NAME] = city.cityName
            pref[PreferencesKeys.SELECTED_CITY_COUNTRY] = city.countryName
            pref[PreferencesKeys.SELECTED_CITY_TIMEZONE] = city.timezoneId
            pref[PreferencesKeys.SELECTED_CITY_LAT] = city.latitude
            pref[PreferencesKeys.SELECTED_CITY_LON] = city.longitude
            pref[PreferencesKeys.SELECTED_CITY_IS_GPS] = city.isGpsLocation
        }
    }
}
