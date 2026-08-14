package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.datastore.preferences.preferencesDataStore
import com.example.MainActivity
import com.example.R
import com.example.data.local.WeatherDatabase
import com.example.data.local.entity.ClockCityEntity
import com.example.data.repository.PredefinedCities
import com.example.data.repository.WeatherRepositoryImpl
import kotlinx.coroutines.runBlocking

class DualClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_dual_clock_4x1)

            // Read saved clock cities from database
            val (city1Name, city1Date, city1Time, city1AmPm, city1Badge, city2Name, city2Date, city2Time, city2AmPm, city2Badge) = runBlocking {
                val db = WeatherDatabase.getInstance(context)
                val clockCities = db.clockCityDao().getAllClockCitiesList()
                
                val defaultCity1 = PredefinedCities.list.firstOrNull() ?: PredefinedCities.list.first { it.id == "seoul" }
                val defaultCity2 = PredefinedCities.list.getOrNull(1) ?: PredefinedCities.list.first { it.id == "london" }
                
                if (clockCities.size >= 2) {
                    val city1 = clockCities[0]
                    val city2 = clockCities[1]
                    val (time1, amPm1, date1, _, isDay1) = WeatherRepositoryImpl.calculateTimeDetails(city1.timezoneId)
                    val (time2, amPm2, date2, _, isDay2) = WeatherRepositoryImpl.calculateTimeDetails(city2.timezoneId)
                    
                    val badge1 = if (isDay1) "����� 22°" else "���� 16°"
                    val badge2 = if (isDay2) "��� 18°" else "���� 11°"
                    
                    city1.cityName to date1 to time1 to amPm1 to badge1 to city2.cityName to date2 to time2 to amPm2 to badge2
                } else if (clockCities.size == 1) {
                    val city1 = clockCities[0]
                    val (time1, amPm1, date1, _, isDay1) = WeatherRepositoryImpl.calculateTimeDetails(city1.timezoneId)
                    val badge1 = if (isDay1) "����� 22°" else "���� 16°"
                    
                    val (time2, amPm2, date2, _, isDay2) = WeatherRepositoryImpl.calculateTimeDetails(defaultCity2.timezoneId)
                    val badge2 = if (isDay2) "��� 18°" else "���� 11°"
                    city1.cityName to date1 to time1 to amPm1 to badge1 to defaultCity2.cityName to date2 to time2 to amPm2 to badge2
                } else {
                    // Fallback to first two predefined cities (now Asunción and Seoul)
                    val (time1, amPm1, date1, _, isDay1) = WeatherRepositoryImpl.calculateTimeDetails(defaultCity1.timezoneId)
                    val (time2, amPm2, date2, _, isDay2) = WeatherRepositoryImpl.calculateTimeDetails(defaultCity2.timezoneId)
                    val badge1 = if (isDay1) "����� 22°" else "���� 16°"
                    val badge2 = if (isDay2) "��� 18°" else "���� 11°"
                    defaultCity1.cityName to date1 to time1 to amPm1 to badge1 to defaultCity2.cityName to date2 to time2 to amPm2 to badge2
                }
            }

            views.setTextViewText(R.id.tv_city_1, city1Name)
            views.setTextViewText(R.id.tv_date_1, city1Date)
            views.setTextViewText(R.id.tv_time_1, city1Time)
            views.setTextViewText(R.id.tv_ampm_1, city1AmPm)
            views.setTextViewText(R.id.tv_badge_1, city1Badge)

            views.setTextViewText(R.id.tv_city_2, city2Name)
            views.setTextViewText(R.id.tv_date_2, city2Date)
            views.setTextViewText(R.id.tv_time_2, city2Time)
            views.setTextViewText(R.id.tv_ampm_2, city2AmPm)
            views.setTextViewText(R.id.tv_badge_2, city2Badge)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
    }
}
