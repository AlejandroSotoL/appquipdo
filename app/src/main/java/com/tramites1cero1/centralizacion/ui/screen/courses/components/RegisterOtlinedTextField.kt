package com.tramites1cero1.centralizacion.ui.screen.courses.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterOtlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorMessage: String? = null,
    readOnly: Boolean = true,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        readOnly = readOnly,
        modifier =modifier
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
