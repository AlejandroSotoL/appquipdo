package com.tramites1cero1.centralizacion.ui.screen.settingsUser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ReusableModal(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = "Aceptar",
    dismissText: String = "Cancelar",
    showDismissButton: Boolean = true,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                content()
            }
        },
        confirmButton = {
            onConfirm?.let {
                TextButton(onClick = it) {
                    Text(confirmText, style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        dismissButton = {
            if (showDismissButton) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    )
}
