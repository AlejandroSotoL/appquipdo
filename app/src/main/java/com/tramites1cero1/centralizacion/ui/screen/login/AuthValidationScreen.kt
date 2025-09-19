package com.tramites1cero1.centralizacion.ui.screen.login

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.ui.theme.White

@Composable
fun AuthValidationScreen(result: ValidationResponseDTO?) {

    val message = result?.sentencesError ?: "Ocurrió un error desconocido."

    if(result?.booleanStatus == true){
        val contentColor = Color(0xFF00796B)
        val icon = Icons.Default.CheckCircle
        Card(
            colors = CardDefaults.cardColors(containerColor = White, contentColor = contentColor),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp).zIndex(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(imageVector = icon, contentDescription = "Estado", tint = contentColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Start
                )
            }
        }
    }else {
        val contentColor = MaterialTheme.colorScheme.error
        val icon = Icons.Default.Warning
        Card(
            colors = CardDefaults.cardColors(containerColor = White, contentColor = contentColor),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth().zIndex(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(imageVector = icon, contentDescription = "Estado", tint = contentColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Start
                )
            }
        }
    }

}