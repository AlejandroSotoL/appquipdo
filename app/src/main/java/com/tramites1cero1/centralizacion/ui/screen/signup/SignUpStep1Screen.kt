package com.tramites1cero1.centralizacion.ui.screen.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.signup.components.RegisterTextField
import com.tramites1cero1.centralizacion.ui.screen.signup.components.StepIndicator
import com.tramites1cero1.centralizacion.ui.theme.White
import com.tramites1cero1.centralizacion.ui.theme.primarycolor


enum class FormFieldType{
    FirstName, MiddleName, LastName, SecondLastName, DocumentNumber, Email, Password, selectedDocumentType, BirthDate, Phone, Address
}

data class FormField(
    val type: FormFieldType,
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val errorMessage : String?,
    val keyBoardOptions : KeyboardOptions = KeyboardOptions.Default
)

@Composable
fun SignUpScreen(
    navController : NavController
) {

    val viewModel : SignUpStep1ViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SignUpStep1Event.NavigateToStep2 -> {
                    navController.navigate(AppRoutes.SIGNUP_STEPTWO)
                }

                is SignUpStep1Event.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val formFields = remember(state){
        listOf(
            FormField(FormFieldType.FirstName, "PrimerNombre", state.firstName ?: "", viewModel::onFirstNameChanged, state.firstNameError),
            FormField(FormFieldType.MiddleName, "SegundoNombre", state.middleName ?: "", viewModel::onMiddleNameChanged,  state.middleNameError),
            FormField(FormFieldType.LastName, "PrimerApellido", state.lastName ?: "", viewModel::onLastNameChanged, state.lastNameError),
            FormField(FormFieldType.SecondLastName, "SegundoApellido", state.secondLastName ?: "", viewModel::onSecondLastNameChanged, state.secondLastNameError)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = primarycolor
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.circles),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(180.dp)
                    .rotate(90f)
                    .offset(x = (-60).dp, y = (-65).dp)
                    .zIndex(1f),
                colorFilter = ColorFilter.tint(Color(0xFF3F51B5))
            )
            Image(
                painter = painterResource(id = R.drawable.circles),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(180.dp)
                    .offset(x = (-60).dp, y = 40.dp)
                    .zIndex(1f),
                colorFilter = ColorFilter.tint(White.copy(alpha = 0.5f))
            )
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {

                IconButton(onClick = viewModel::onCancelClicked) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier
                            .background(Color(0xFF3F51B5), shape = CircleShape)
                            .size(36.dp)
                            .padding(6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if(Firebase.auth.currentUser != null) "Completar registro" else "Registrarse",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StepIndicator(stepNumber = 1, isActive = true)
                    Spacer(modifier = Modifier.width(50.dp))
                    StepIndicator(stepNumber = 2, isActive = false)
                    Spacer(modifier = Modifier.width(50.dp))
                    StepIndicator(stepNumber = 3, isActive = false)
                }

                Spacer(modifier = Modifier.height(24.dp))

                formFields.forEach { field ->
                    RegisterTextField(
                        value = field.value,
                        onValueChange = field.onValueChange,
                        label = field.label,
                        errorMessage = field.errorMessage,
                        keyboardOptions = field.keyBoardOptions,
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = viewModel::onNextClicked,
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F7DFF))
                ) {
                    Text("Siguiente", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = viewModel::onCancelClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF3F7DFF))
                ) {
                    Text("Cancelar", color = Color(0xFF3F7DFF))
                }
            }
        }

    }
}