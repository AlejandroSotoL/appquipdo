package com.tramites1cero1.centralizacion.ui.screen.courses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
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
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tramites1cero1.centralizacion.ui.components.PolicyCheckboxes
import com.tramites1cero1.centralizacion.ui.screen.courses.CoursesViewModel
import com.tramites1cero1.centralizacion.ui.screen.courses.RegistrationFormState
import com.tramites1cero1.centralizacion.ui.screen.courses.fieldConfig
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel

@Composable
fun RegistrationForm(
    courseTitle: String,
    formState: RegistrationFormState,
    onFormValueChange: ((RegistrationFormState) -> RegistrationFormState) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    authViewModel: AuthViewModel  = hiltViewModel(),
    coursesViewModel : CoursesViewModel = hiltViewModel()
) {
    val user by authViewModel.user.collectAsState()
    val fields = listOf(
        fieldConfig(formState.documentNumber, "Documento", coursesViewModel::onDocumentNumberChanged, Icons.Default.DriveFileRenameOutline, "Documento", KeyboardType.Number, formState.isEditable, formState.documentNumberError),
        fieldConfig(formState.firstName, "Nombre", coursesViewModel::onFirstnameChanged, Icons.Default.DriveFileRenameOutline, "Nombre", KeyboardType.Text,formState.isEditable, formState.firstNameError),
        fieldConfig(formState.lastName, "Apellido", coursesViewModel::onLastnameChanged, Icons.Default.DriveFileRenameOutline, "Apellido", KeyboardType.Text, formState.isEditable, formState.lastNameError),
        fieldConfig(formState.age, "Edad", coursesViewModel::onAgeChanged, Icons.Default.Numbers, "Edad", KeyboardType.Number, formState.isEditable, formState.ageError),
        fieldConfig(formState.email, "Correo Electrónico", coursesViewModel::onEmailChanged, Icons.Default.Email, "Correo", KeyboardType.Email, formState.isEditable, formState.emailError),
        fieldConfig(formState.phone, "Teléfono", coursesViewModel::onPhoneChanged, Icons.Default.Phone, "Teléfono", KeyboardType.Phone, formState.isEditable, formState.phoneError)
    )
    LaunchedEffect(user) {
        coursesViewModel.initForm(user)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Inscripción a: $courseTitle",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))

        fields.forEach { field ->
            RegisterOtlinedTextField(
                value = field.value,
                onValueChange = field.onValueChange,
                label = { Text(field.label) },
                leadingIcon = { Icon(field.icon, contentDescription = field.contentDescription) },
                keyboardOptions = KeyboardOptions(keyboardType = field.keyboardType),
                errorMessage = field.errorMessage,
                readOnly = !field.readOnly,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small as RoundedCornerShape
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        PolicyCheckboxes(
            dataPolicyChecked = formState.hasAcceptedTerms,
            onDataPolicyChange = coursesViewModel::onTermsAccepted,
            privacyPolicyChecked = formState.hasAcceptedPrivacyPolicy,
            onPrivacyPolicyChange = coursesViewModel::onPrivacyPolicyAccepted,
            dataPolicyUrl = "",
            privacyPolicyUrl = ""
        )

        if (formState.termsError != null) {
            Text(
                text = formState.termsError ?: "Acepta todos los términos y políticas",
                color = MaterialTheme.colorScheme.error
            )
        }

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = onSubmit, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Enviar")
                }
            }
        }
    }
}
