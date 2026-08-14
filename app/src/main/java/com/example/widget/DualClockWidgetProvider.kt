package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.repository.WeatherRepositoryImpl

class DualClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_dual_clock_4x1)

            // Seoul (Day)
            val (seoulTime, seoulAmPm, seoulDate, _, isSeoulDay) =
                WeatherRepositoryImpl.calculateTimeDetails("Asia/Seoul")
            views.setTextViewText(R.id.tv_city_1, "Seoul")
            views.setTextViewText(R.id.tv_date_1, seoulDate)
            views.setTextViewText(R.id.tv_time_1, seoulTime)
            views.setTextViewText(R.id.tv_ampm_1, seoulAmPm)
            views.setTextViewText(R.id.tv_badge_1, if (isSeoulDay) "☀️ 22°" else "🌙 16°")

            // London (Night / Diff)
            val (londonTime, londonAmPm, londonDate, _, isLondonDay) =
                WeatherRepositoryImpl.calculateTimeDetails("Europe/London")
            views.setTextViewText(R.id.tv_city_2, "London")
            views.setTextViewText(R.id.tv_date_2, londonDate)
            views.setTextViewText(R.id.tv_time_2, londonTime)
            views.setTextViewText(R.id.tv_ampm_2, londonAmPm)
            views.setTextViewText(R.id.tv_badge_2, if (isLondonDay) "⛅ 18°" else "🌙 11°")

            // Click to open main app
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
