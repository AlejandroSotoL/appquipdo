package com.tramites1cero1.centralizacion.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tramites1cero1.centralizacion.R

@Composable
fun NoConnectionDialog(
    isConnectionRestored: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_no_wifi),
                    contentDescription = "Sin conexión",
                    modifier = Modifier.size(90.dp)
                )
                Text(
                    text = "No estás conectado a internet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Señor(a) ciudadano. Para un correcto funcionamiento de nuestra App es necesario estar conectado a internet, por favor verifique su conexión y vuelva a intentarlo.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onDismiss,
                    enabled = isConnectionRestored,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entendido")
                }
            }
        }
    }
}