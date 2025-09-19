package com.tramites1cero1.centralizacion.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun abrirURL(context: Context, url: String?, toolbarColor: Int) {
    val uri = url?.let { Uri.parse(it) } ?: return

    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(toolbarColor)
    builder.setUrlBarHidingEnabled(true)
    builder.setShowTitle(true)

    val customTabsIntent = builder.build()

    customTabsIntent.launchUrl(context, uri)
}
