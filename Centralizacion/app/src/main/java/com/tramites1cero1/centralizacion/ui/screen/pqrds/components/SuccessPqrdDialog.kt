package com.tramites1cero1.centralizacion.ui.screen.pqrds.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SuccessDialog(ticket: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Éxito!") },
        text = { Text("Tu solicitud ha sido enviada correctamente. Tu número de ticket es: $ticket") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}