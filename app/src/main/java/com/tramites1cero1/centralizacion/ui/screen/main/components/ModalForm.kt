package com.tramites1cero1.centralizacion.ui.screen.main.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tramites1cero1.centralizacion.ui.theme.Roboto_semiBold
import com.tramites1cero1.centralizacion.ui.theme.White
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.ui.components.PolicyCheckboxRow

import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel

import com.tramites1cero1.centralizacion.ui.components.PolicyCheckboxes
import com.tramites1cero1.centralizacion.ui.screen.main.ModalFormMode
import com.tramites1cero1.centralizacion.ui.theme.primarycolor
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalForm(
    onDismissRequest: () -> Unit,
    onConfirm: (ModalData) -> Unit,
    viewModel: ModalFormViewModel = hiltViewModel(),
    mode: ModalFormMode,
    authViewModel: AuthViewModel  = hiltViewModel(),
    dataPolicyUrl: String,
    privacyPolicyUrl: String
) {
    val user by authViewModel.user.collectAsState()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isActiveSavePeopleInvitated by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        viewModel.initForm(user)
    }

    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
        unfocusedIndicatorColor = Color.Transparent,
    )

    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        checkmarkColor = Color.White,
        uncheckedColor = Color.Gray
    )

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                "Por favor, ingrese los siguientes datos y presione el botón continuar.",
                fontFamily = Roboto_semiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                OutlinedTextField(
                    value = state.identificacion,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onFormDataChange(state.copy(identificacion = it)) },
                    label = {
                        Text(
                            "Número de Identificación",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    isError = state.identificacionError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(fraction = 0.95f),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                    readOnly = !state.isEditable,
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = state.nombresApellidos,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onFormDataChange(state.copy(nombresApellidos = it)) },
                    label = {
                        Text(
                            "Nombres y apellidos",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    isError = state.nombresApellidosError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(fraction = 0.95f),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                    readOnly = !state.isEditable,
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = state.telefono,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onFormDataChange(state.copy(telefono = it)) },
                    label = {
                        Text(
                            "Número de teléfono",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = state.telefonoError != null,
                    modifier = Modifier.fillMaxWidth(fraction = 0.95f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                    readOnly = !state.isEditable,

                    )

                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = state.correo,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { viewModel.onFormDataChange(state.copy(correo = it)) },
                    label = {
                        Text(
                            "Correo electrónico",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = state.correoError != null,
                    singleLine = true,
                    supportingText = { state.correoError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(fraction = 0.95f),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                    readOnly = !state.isEditable
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
            ) {
                PolicyCheckboxes(
                    dataPolicyChecked = state.aceptaPoliticas,
                    onDataPolicyChange = { isChecked ->
                        viewModel.onFormDataChange(state.copy(aceptaPoliticas = isChecked))
                    },
                    privacyPolicyChecked = state.aceptaCondiciones,
                    onPrivacyPolicyChange = { isChecked ->
                        viewModel.onFormDataChange(state.copy(aceptaCondiciones = isChecked))
                    },
                    dataPolicyUrl = dataPolicyUrl,
                    privacyPolicyUrl = privacyPolicyUrl
                )

                PolicyCheckboxRow(
                    text = "¿Deseas que esta informacion sea almacenada, En nuestro sistema?",
                    linkText = "",
                    url = "",
                    checked = state.isValidSaveInformation,
                    onCheckedChange = { isValid ->
                    viewModel.onFormDataChange(state.copy(isValidSaveInformation = isValid))
                    }
                )

            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (viewModel.validate()) {
                        if (state.isValidSaveInformation) {
                            viewModel.createPeopleInvitated()
                        }
                    } else {
                        Toast.makeText(context, "Tenemos problemas", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = primarycolor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Continuar")
                }
            }

            val registrationResult by viewModel.registrationResult.collectAsState()
            LaunchedEffect(registrationResult) {
                registrationResult?.let { response ->
                    if (!response.booleanStatus) {
                        Toast.makeText(
                            context,
                            "-${response.sentencesError}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Procesando el registro.",
                            Toast.LENGTH_SHORT
                        ).show()
                        kotlinx.coroutines.delay(600)
                        onConfirm(state)
                    }
                }
            }
        }
    }
}