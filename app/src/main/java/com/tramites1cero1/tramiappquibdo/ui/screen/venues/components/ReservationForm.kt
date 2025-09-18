package com.tramites1cero1.tramiappquibdo.ui.screen.venues.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tramites1cero1.tramiappquibdo.ui.components.PolicyCheckboxes
import com.tramites1cero1.tramiappquibdo.ui.screen.courses.components.RegisterOtlinedTextField
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.venues.VenuesViewModel

import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationForm(
    venueTitle: String,
    isLoading: Boolean,
    authViewModel: AuthViewModel  = hiltViewModel(),
    venuesViewModel: VenuesViewModel
) {
    // Lista de opciones para el tipo de documento
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val documentTypes by venuesViewModel.documentTypes.collectAsState()
    val formState by venuesViewModel.formState.collectAsState()
    val user by authViewModel.user.collectAsState()
    LaunchedEffect(user) {
        venuesViewModel.initForm(user)
    }

    // Lógica para los selectores de fecha y hora
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            venuesViewModel.onDateChange(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val time = "%02d:%02d:00".format(hourOfDay, minute)
            venuesViewModel.onTimeChange(time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // Formato de 24 horas
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = venueTitle,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray)
            )
        }

            RegisterOtlinedTextField(
                value = formState.firstName,
                onValueChange = venuesViewModel::onFirstNameChange,
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre") },
                readOnly = !formState.isEditable,
                errorMessage = formState.firstNameError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()

            )
            RegisterOtlinedTextField(
                value = formState.lastName,
                onValueChange = venuesViewModel::onLastNameChange,
                label = { Text("Apellido") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Apellido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                errorMessage = formState.lastNameError,
                readOnly = !formState.isEditable,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)

            )


        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = formState.documentType,
                onValueChange = venuesViewModel::onDocumentTypeChange,
                readOnly = true,
                label = { Text("Tipo Documento") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isDropdownExpanded) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                isError = formState.documentTypeError != null

            )



            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                documentTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            venuesViewModel.onDocumentTypeChange(type.name)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
        if (formState.documentTypeError != null) {
            Text(
                text = formState.documentTypeError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        RegisterOtlinedTextField(
            value = formState.documentNumber,
            onValueChange = venuesViewModel::onDocumentNumberChange,
            label = { Text("Número de Documento") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            errorMessage = formState.documentNumberError,
            readOnly = !formState.isEditable,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
        )

        RegisterOtlinedTextField(
            value = formState.email,
            onValueChange = venuesViewModel::onEmailChange,
            label = { Text("Correo Electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo Elect") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage = formState.emailError,
            readOnly = !formState.isEditable,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)

        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (formState.date.isEmpty()) "Fecha" else formState.date)
            }
            OutlinedButton(
                onClick = { timePickerDialog.show() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (formState.time.isEmpty()) "Hora" else formState.time)
            }
        }
        val errorMessage = when {
            formState.dateError != null -> formState.dateError
            formState.timeError != null -> formState.timeError
            formState.termsError != null -> formState.termsError
            else -> null
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        PolicyCheckboxes(
            dataPolicyChecked = formState.hasAcceptedTerms,
            onDataPolicyChange = venuesViewModel::onTermsAccepted,
            privacyPolicyChecked = formState.hasAcceptedPrivacyPolicy,
            onPrivacyPolicyChange = venuesViewModel::onPrivacyPolicyAccepted,
            dataPolicyUrl = "",
            privacyPolicyUrl = ""
        )


        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = venuesViewModel::onDismissBottomSheet, enabled = !isLoading) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = venuesViewModel::submitReservation, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Reservar")
                }
            }
        }
    }
}