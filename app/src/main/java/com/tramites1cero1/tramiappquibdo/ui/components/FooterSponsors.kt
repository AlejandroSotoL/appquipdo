package com.tramites1cero1.tramiappquibdo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tramites1cero1.tramiappquibdo.R
import kotlinx.coroutines.delay

@Composable
fun FooterSponsors(Bank: String?= "", color: Color){
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(3000)
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(800, delayMillis = 100)) +
                slideInVertically(
                    animationSpec = tween(800, delayMillis = 800),
                    initialOffsetY = { fullHeight -> -fullHeight }
                ) +
                scaleIn(tween(800, delayMillis = 100))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.height(30.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.icobancolombia),
                    contentDescription = "Logo Bancolombia",
                    tint = color,
                    modifier = Modifier.size(115.dp)
                )
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp), // Optional padding
                    thickness = 1.dp, // Line thickness
                    color = color // Line color
                )
                Icon(
                    painter = painterResource(R.drawable.ico101software),
                    contentDescription = "Logo Software",
                    tint = color,
                    modifier = Modifier.size(105.dp)
                )
            }
        }
    }
}