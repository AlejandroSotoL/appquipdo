package com.tramites1cero1.centralizacion.ui.screen.signup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StepIndicator(stepNumber: Int, isActive: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .background(
                color = if (isActive) Color(0xFF3F7DFF) else Color.Gray,
                shape = CircleShape
            )
    ) {
        Text(
            text = stepNumber.toString(),
            color = Color.White,
            fontSize = 19.sp
        )
    }
}