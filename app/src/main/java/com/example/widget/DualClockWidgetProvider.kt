package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.WeatherDatabase
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
            val widgetData = runBlocking {
                val db = WeatherDatabase.getInstance(context)
                val clockCities = db.clockCityDao().getAllClockCitiesList()

                val defaultCity1 = PredefinedCities.list.firstOrNull() ?: PredefinedCities.list.first { it.id == "asu" }
                val defaultCity2 = PredefinedCities.list.getOrNull(1) ?: PredefinedCities.list.first { it.id == "seoul" }

                if (clockCities.size >= 2) {
                    val city1 = clockCities[0]
                    val city2 = clockCities[1]
                    val (time1, amPm1, date1, _, isDay1) = WeatherRepositoryImpl.calculateTimeDetails(city1.timezoneId)
                    val (time2, amPm2, date2, _, isDay2) = WeatherRepositoryImpl.calculateTimeDetails(city2.timezoneId)

                    val badge1 = if (isDay1) "☀️ 22°" else "🌙 16°"
                    val badge2 = if (isDay2) "⛅ 18°" else "🌙 11°"

                    WidgetData(
                        city1Name = city1.cityName,
                        city1Date = date1,
                        city1Time = time1,
                        city1AmPm = amPm1,
                        city1Badge = badge1,
                        city2Name = city2.cityName,
                        city2Date = date2,
                        city2Time = time2,
                        city2AmPm = amPm2,
                        city2Badge = badge2
                    )
                } else if (clockCities.size == 1) {
                    val city1 = clockCities[0]
                    val (time1, amPm1, date1, _, isDay1) = WeatherRepositoryImpl.calculateTimeDetails(city1.timezoneId)
                    val badge1 = if (isDay1) "☀️ 22°" else "🌙 16°"

                    val (time2, amPm2, date2, _, isDay2) = WeatherRepositoryImpl.calculateTimeDetails(defaultCity2.timezoneId)
                    val badge2 = if (isDay2) "⛅ 18°" else "🌙 11°"

                    WidgetData(
                        city1Name = city1.cityName,
                        city1Date = date1,
                        city1Time = time1,
                        city1AmPm = amPm1,
                        city1Badge = badge1,
                        city2Name = defaultCity2.cityName,
                        city2Date = date2,
                        city2Time = time2,
                        city2AmPm = amPm2,
                        city2Badge = badge2
                    )
                } else {
                    // Fallback to first two predefined cities (now Asunción and Seoul)
                    val (time1, amPm1, date1, _, isDay1) = WeatherRepositoryImpl.calculateTimeDetails(defaultCity1.timezoneId)
                    val (time2, amPm2, date2, _, isDay2) = WeatherRepositoryImpl.calculateTimeDetails(defaultCity2.timezoneId)
                    val badge1 = if (isDay1) "☀️ 22°" else "🌙 16°"
                    val badge2 = if (isDay2) "⛅ 18°" else "🌙 11°"

                    WidgetData(
                        city1Name = defaultCity1.cityName,
                        city1Date = date1,
                        city1Time = time1,
                        city1AmPm = amPm1,
                        city1Badge = badge1,
                        city2Name = defaultCity2.cityName,
                        city2Date = date2,
                        city2Time = time2,
                        city2AmPm = amPm2,
                        city2Badge = badge2
                    )
                }
            }

            views.setTextViewText(R.id.tv_city_1, widgetData.city1Name)
            views.setTextViewText(R.id.tv_date_1, widgetData.city1Date)
            views.setTextViewText(R.id.tv_time_1, widgetData.city1Time)
            views.setTextViewText(R.id.tv_ampm_1, widgetData.city1AmPm)
            views.setTextViewText(R.id.tv_badge_1, widgetData.city1Badge)

            views.setTextViewText(R.id.tv_city_2, widgetData.city2Name)
            views.setTextViewText(R.id.tv_date_2, widgetData.city2Date)
            views.setTextViewText(R.id.tv_time_2, widgetData.city2Time)
            views.setTextViewText(R.id.tv_ampm_2, widgetData.city2AmPm)
            views.setTextViewText(R.id.tv_badge_2, widgetData.city2Badge)

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

    private data class WidgetData(
        val city1Name: String,
        val city1Date: String,
        val city1Time: String,
        val city1AmPm: String,
        val city1Badge: String,
        val city2Name: String,
        val city2Date: String,
        val city2Time: String,
        val city2AmPm: String,
        val city2Badge: String
    )
}