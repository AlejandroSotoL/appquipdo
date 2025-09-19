package com.tramites1cero1.centralizacion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StepIndicator(currentStep: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val activeColor = MaterialTheme.colorScheme.primary
        val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

        StepItem(text = "Paso 1", isActive = currentStep >= 1, isCompleted = currentStep >= 1)
        HorizontalDivider(modifier = Modifier.weight(1f), color = if (currentStep > 1) activeColor else inactiveColor)
        StepItem(text = "Paso 2", isActive = currentStep >= 2, isCompleted = currentStep >= 2)
        HorizontalDivider(modifier = Modifier.weight(1f), color = if (currentStep > 2) activeColor else inactiveColor)
        StepItem(text = "Paso 3", isActive = currentStep >= 3, isCompleted = currentStep >= 3)
    }
}

@Composable
private fun StepItem(text: String, isActive: Boolean, isCompleted: Boolean) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val backgroundColor = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderColor = if (isActive) MaterialTheme.colorScheme.primary else color
    val iconColor = MaterialTheme.colorScheme.onPrimary

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(2.dp, borderColor, CircleShape)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = "Completado", tint = iconColor)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}