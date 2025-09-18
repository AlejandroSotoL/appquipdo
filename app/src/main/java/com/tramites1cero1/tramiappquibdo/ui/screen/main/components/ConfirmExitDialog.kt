package com.tramites1cero1.tramiappquibdo.ui.screen.main.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmExitDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("¡ATENCIÓN!", style = MaterialTheme.typography.titleLarge) },
        text = { Text("¿Deseas salir de la App?", style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Aceptar", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        },
        dismissButton  = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancelar", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        },
    )
}