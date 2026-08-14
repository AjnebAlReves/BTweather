package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_weather_2x2)

            views.setTextViewText(R.id.tv_weather_city, "Olathe")
            views.setTextViewText(R.id.tv_weather_desc, "Partly Cloudy")
            views.setTextViewText(R.id.tv_weather_temp, "71°")
            views.setTextViewText(R.id.tv_weather_range, "H:83° L:54°")
            views.setTextViewText(R.id.tv_weather_icon, "⛅")

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
}
