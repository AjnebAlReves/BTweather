package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.model.AlertSeverity
import com.example.domain.model.SevereWeatherAlert

object WeatherNotificationManager {

    const val CHANNEL_ID = "severe_weather_alerts_channel"
    const val CHANNEL_NAME = "Severe Weather Alerts"
    const val NOTIFICATION_ID_BASE = 8000
    const val TEST_ALERT_ID = 8888

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts and warnings for severe weather conditions in tracked locations"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showSevereWeatherAlert(context: Context, alert: SevereWeatherAlert, soundAndVibrate: Boolean = true) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_ALERT_LOCATION", alert.locationName)
            putExtra("EXTRA_ALERT_ID", alert.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val severityEmoji = when (alert.severity) {
            AlertSeverity.EXTREME -> "🚨 EXTREME"
            AlertSeverity.WARNING -> "⚠️ SEVERE WARNING"
            AlertSeverity.WATCH -> "⚡ WEATHER WATCH"
            AlertSeverity.ADVISORY -> "ℹ️ ADVISORY"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("$severityEmoji: ${alert.title} in ${alert.locationName}")
            .setContentText(alert.description)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("⚠️ ${alert.title} — ${alert.locationName}")
                    .bigText("${alert.description}\n\nIssued by: ${alert.sender}\nEffective: ${alert.effectiveTime} | Expires: ${alert.expiresTime}")
                    .setSummaryText("Weather Alert • ${alert.severity.label}")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(if (alert.severity == AlertSeverity.EXTREME) Color.RED else Color.parseColor("#E65100"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(alert.severity == AlertSeverity.EXTREME || alert.severity == AlertSeverity.WARNING)
            .addAction(
                android.R.drawable.ic_menu_view,
                "View Forecast",
                pendingIntent
            )

        if (!soundAndVibrate) {
            builder.setSilent(true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(alert.id.hashCode(), builder.build())
    }

    fun showTestAlertNotification(context: Context) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            TEST_ALERT_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("⚠️ Test Severe Weather Alert: Active Monitoring")
            .setContentText("Severe weather notifications are active. You will receive real-time warnings for your tracked locations.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("This is a live test of the Severe Weather Alert Notification System. High wind, severe thunderstorm, flash flood, and blizzard alerts will appear persistently here.")
                    .setSummaryText("System Test • All Clear")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setColor(Color.parseColor("#E65100"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(TEST_ALERT_ID, builder.build())
    }

    fun dismissAlert(context: Context, alertId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alertId.hashCode())
    }
}
