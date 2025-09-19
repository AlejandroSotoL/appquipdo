package com.tramites1cero1.centralizacion.ui.screen.settingsUser.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tramites1cero1.centralizacion.ui.theme.ColorTextSecondaryVariant
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.Red
import com.tramites1cero1.centralizacion.ui.theme.White
import com.tramites1cero1.centralizacion.ui.theme.buttoncolorslogin

@Composable
fun EmailInputField(email: String, isError: Boolean, onEmailChanged: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChanged,
        label = { Text("Correo electrónico", color = ColorTextSecondaryVariant) },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = buttoncolorslogin,
            unfocusedBorderColor = Gray600,
            cursorColor = buttoncolorslogin,
            focusedLabelColor = buttoncolorslogin,
            focusedTextColor = White,
            unfocusedTextColor = White
        )
    )

    if (isError) {
        Text(
            text = "Por favor ingrese un correo válido",
            color = Red,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(top = 4.dp)
        )
    }
}

