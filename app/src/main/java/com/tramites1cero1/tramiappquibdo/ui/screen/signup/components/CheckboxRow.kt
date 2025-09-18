package com.tramites1cero1.tramiappquibdo.ui.screen.signup.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tramites1cero1.tramiappquibdo.ui.theme.buttoncolorslogin

@Composable
fun CheckboxRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onCheckedChange(!checked) }) {
        RadioButton(selected = checked, onClick = { onCheckedChange(!checked) },
            colors = RadioButtonDefaults.colors(
            selectedColor = buttoncolorslogin,
            unselectedColor = buttoncolorslogin
        ))
        Text(text, color = buttoncolorslogin)
    }
}