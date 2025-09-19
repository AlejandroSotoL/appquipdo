package com.tramites1cero1.centralizacion.ui.screen.psv.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tramites1cero1.centralizacion.ui.screen.psv.FormButtons
import com.tramites1cero1.centralizacion.ui.screen.psv.PsvPaymentViewModel
import com.tramites1cero1.centralizacion.ui.screen.psv.PsvUiState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Form(
    state: PsvUiState,
    viewModel: PsvPaymentViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            text = "Datos del ciudadano o contribuyente",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = state.selectedDocumentType?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("*Tipo de documento", style = MaterialTheme.typography.bodySmall) },
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                colors = transparentTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            )

            AnimatedVisibility(
                visible = isDropdownExpanded,
                enter = fadeIn(animationSpec = tween(200)) + expandVertically(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200)),
            ) {

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(4.dp))
                ) {
                    state.documentTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                viewModel.onDocumentTypeSelected(option)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.documentNumber,
            onValueChange = viewModel::onDocumentNumberChange,
            label = { Text("*Número identificación", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.firstName,
            onValueChange = viewModel::onFirstNameChange,
            label = { Text("*Primer nombre", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.secondName,
            onValueChange = viewModel::onSecondNameChange,
            label = { Text("Segundo nombre", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.firstLastName,
            onValueChange = viewModel::onFirstLastNameChange,
            label = { Text("*Primer apellido", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.secondLastName,
            onValueChange = viewModel::onSecondLastNameChange,
            label = { Text("Segundo apellido", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))
        FormButtons(onNext = onNext, onCancel = onCancel, isNextEnabled = state.isNextButtonEnabled)
    }
}

@Composable
private fun transparentTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedBorderColor = MaterialTheme.colorScheme.primary
)