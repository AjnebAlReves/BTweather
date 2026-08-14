package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.MainActivity
import com.example.R
import com.example.data.local.WeatherDatabase
import com.example.data.local.entity.CachedWeatherEntity
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WeatherData
import kotlinx.coroutines.runBlocking

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_weather_2x2)

            // Read last selected city and cached weather from persistence
            val (cityName, weatherDesc, temp, tempRange, icon) = runBlocking {
                val dataStore = context.preferencesDataStore(name = "user_settings_pref")
                val data = dataStore.data.first()
                
                val cityId = data[stringPreferencesKey("selected_city_id")]
                val cityName = data[stringPreferencesKey("selected_city_name")] ?: cityId ?: ""
                val lat = data[doublePreferencesKey("selected_city_lat")] ?: 0.0
                val lon = data[doublePreferencesKey("selected_city_lon")] ?: 0.0
                
                if (cityName.isNotBlank()) {
                    val db = WeatherDatabase.getInstance(context)
                    val cached = db.weatherDao().getWeatherForCityOnce(cityName)
                    cached?.let { entity ->
                        val weatherData = mapEntityToWeatherData(entity)
                        val desc = weatherData.condition.label
                        val tempStr = "${weatherData.currentTempC.toInt()}°"
                        val rangeStr = "H:${weatherData.maxTempC.toInt()}° L:${weatherData.minTempC.toInt()}°"
                        val iconStr = weatherData.conditionIcon
                        return@runBlocking cityName to desc to tempStr to rangeStr to iconStr
                    }
                }
                
                // Fallback to first saved clock city
                val db = WeatherDatabase.getInstance(context)
                val clockCities = db.clockCityDao().getAllClockCitiesList()
                if (clockCities.isNotEmpty()) {
                    val city = clockCities.first()
                    val cached = db.weatherDao().getWeatherForCityOnce(city.cityName)
                    cached?.let { entity ->
                        val weatherData = mapEntityToWeatherData(entity)
                        val desc = weatherData.condition.label
                        val tempStr = "${weatherData.currentTempC.toInt()}°"
                        val rangeStr = "H:${weatherData.maxTempC.toInt()}° L:${weatherData.minTempC.toInt()}°"
                        val iconStr = weatherData.conditionIcon
                        return@runBlocking city.cityName to desc to tempStr to rangeStr to iconStr
                    }
                }
                
                // Default fallback
                "Olathe" to "Partly Cloudy" to "71°" to "H:83° L:54°" to "�����"
            }

            views.setTextViewText(R.id.tv_weather_city, cityName)
            views.setTextViewText(R.id.tv_weather_desc, weatherDesc)
            views.setTextViewText(R.id.tv_weather_temp, temp)
            views.setTextViewText(R.id.tv_weather_range, tempRange)
            views.setTextViewText(R.id.tv_weather_icon, icon)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_weather_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    private fun mapEntityToWeatherData(entity: CachedWeatherEntity): WeatherData {
        val condition = try {
            WeatherCondition.valueOf(entity.conditionName)
        } catch (e: Exception) {
            WeatherCondition.CLEAR_DAY
        }
        return WeatherData(
            cityName = entity.cityName,
            countryName = entity.countryName,
            latitude = entity.latitude,
            longitude = entity.longitude,
            timezone = entity.timezone,
            currentTempC = entity.currentTempC,
            feelsLikeTempC = entity.feelsLikeTempC,
            minTempC = entity.minTempC,
            maxTempC = entity.maxTempC,
            condition = condition,
            summaryText = entity.summaryText,
            isDay = entity.isDay,
            humidityPercent = entity.humidityPercent,
            windSpeedKmh = entity.windSpeedKmh,
            windDirectionDegrees = entity.windDirectionDegrees,
            uvIndex = entity.uvIndex,
            pressureHpa = entity.pressureHpa,
            airQualityIndex = entity.airQualityIndex,
            airQualityLabel = if (entity.airQualityIndex <= 50) "Good" else "Moderate",
            sunriseTime = entity.sunriseTime,
            sunsetTime = entity.sunsetTime,
            hourlyForecast = emptyList(),
            dailyForecast = emptyList(),
            alerts = emptyList(),
            isGpsLocation = entity.isGpsLocation,
            lastUpdatedMillis = entity.lastUpdatedMillis,
            providerUsed = entity.providerUsed
        )
    }

    companion object {
        private val Context.preferencesDataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "user_settings_pref")
    }
}
    }
}
