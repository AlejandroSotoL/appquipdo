package com.tramites1cero1.centralizacion.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminders_channel",
                "Recordatorios de pago",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Canal para recordatorios de pagos y trámites"

            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
