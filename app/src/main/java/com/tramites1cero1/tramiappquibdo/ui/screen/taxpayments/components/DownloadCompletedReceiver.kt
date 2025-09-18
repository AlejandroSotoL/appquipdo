package com.tramites1cero1.tramiappquibdo.ui.screen.taxpayments.components

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow


object DownloadEventBus {
    val events = MutableSharedFlow<Uri>()
}

class DownloadCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L && context != null) {
                val downloadManager = context.getSystemService(DownloadManager::class.java)

                val uri = downloadManager.getUriForDownloadedFile(id)
                if (uri != null) {
                    Log.d("DownloadReceiver", "Descarga completa. Uri obtenida: $uri")
                    DownloadEventBus.events.tryEmit(uri)
                }
            }
        }
    }
}