package com.tramites1cero1.centralizacion.utils

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.screen.initial.SplashScreen
import kotlin.jvm.java

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Notificación"
        val content = intent.getStringExtra("EXTRA_CONTENT") ?: "Es hora de tu recordatorio."
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 0)
        showNotification(context, title, content, notificationId)
    }

    private fun showNotification(context: Context, title: String, content: String, notificationId : Int) {
        val channelId = "reminders_channel"


        // Intent para abrir la app
        val mainActivityIntent = Intent(context, SplashScreen::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logotramiappcentralizacion)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para que aparezca
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notificationId, builder.build()) // Usar el ID único
        }
    }

}