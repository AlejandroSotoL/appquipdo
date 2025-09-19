package com.tramites1cero1.centralizacion.ui.screen.settingsUser.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tramites1cero1.centralizacion.ui.theme.Black
import com.tramites1cero1.centralizacion.ui.theme.White

@Composable
fun SendCodeButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors( contentColor  = White),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Black, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text("Enviar código", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}