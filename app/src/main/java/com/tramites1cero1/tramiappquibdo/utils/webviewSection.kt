package com.tramites1cero1.tramiappquibdo.utils

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray300

@Composable
fun webview(
    navController: NavController,
    url: String,
    titulo: String,
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }

    // 2. El BackHandler ahora tiene acceso directo a la instancia 'webView' recordada.
    BackHandler(enabled = true) {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            navController.popBackStack()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ){
        Box(){
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp, bottom = 30.dp, start = 16.dp, end = 16.dp).background(color = Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        navController.popBackStack()
                    }
                },
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape
                        ).size(40.dp)) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(25.dp).padding(4.dp))
                }
                Text(text= titulo, modifier = Modifier.fillMaxWidth().padding( horizontal = 16.dp),
                    textAlign = TextAlign.Start, style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary)
            }

        }
        Column(
            modifier = Modifier
                .shadow(24.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), clip = true)

        ) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(url: String) {
    val context = LocalContext.current

    AndroidView(factory = {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.cacheMode = WebSettings.LOAD_NORMAL
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    },
        modifier = Modifier.fillMaxSize())
}