package com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tramites1cero1.tramiappquibdo.ui.theme.ColorTextSecondaryVariant
import com.tramites1cero1.tramiappquibdo.ui.theme.Red

@Composable
fun InfoText(message: String?, isSuccess: Boolean) {
    message?.let {
        Text(
            text = it,
            color = if (isSuccess) ColorTextSecondaryVariant else Red,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}