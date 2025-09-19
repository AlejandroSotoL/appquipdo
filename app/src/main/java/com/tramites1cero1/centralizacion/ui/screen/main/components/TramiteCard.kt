package com.tramites1cero1.centralizacion.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.domain.model.InfoTramite
import com.tramites1cero1.centralizacion.domain.model.TramiteAccion
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.theme.Roboto_regular

@Composable
fun TramiteCard(
    cardinfo: InfoTramite,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .clickable(
                    enabled = true,
                    onClick = {
                        onClick() })
                .background(color = cardinfo.color, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(id = cardinfo.icono),
                contentDescription = cardinfo.nombre,
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = cardinfo.nombre,
            fontSize = 14.sp,
            fontFamily = Roboto_regular,
            style = TextStyle(
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}