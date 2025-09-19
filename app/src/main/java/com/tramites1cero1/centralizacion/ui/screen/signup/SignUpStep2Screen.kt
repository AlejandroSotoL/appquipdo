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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.signup.components.DocumentTypeDropdown
import com.tramites1cero1.centralizacion.ui.screen.signup.components.RegisterPasswordField
import com.tramites1cero1.centralizacion.ui.screen.signup.components.RegisterTextField
import com.tramites1cero1.centralizacion.ui.screen.signup.components.StepIndicator
import com.tramites1cero1.centralizacion.ui.theme.RobotoBold
import com.tramites1cero1.centralizacion.ui.theme.White
import com.tramites1cero1.centralizacion.ui.theme.primarycolor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTwoScreen(
    navController : NavController,
    viewModel: SignUpStep2ViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SignUpStep2Event.NavigateToStep3 -> navController.navigate(AppRoutes.SIGNUP_STEPTHREE)
                is SignUpStep2Event.NavigateBack -> navController.popBackStack()
            }
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }
    val formFields = remember(state) {
        listOf(
            FormField(FormFieldType.DocumentNumber, "Número de documento", state.documentNumber, viewModel::onDocumentNumberChanged, state.documentNumberError, KeyboardOptions(keyboardType = KeyboardType.Number)),
            FormField(FormFieldType.Email, "Correo electrónico", state.email, viewModel::onEmailChanged, state.emailError, KeyboardOptions(keyboardType = KeyboardType.Email)),
            FormField(FormFieldType.Password, "Contraseña", state.password, viewModel::onPasswordChanged, state.passwordError, KeyboardOptions(keyboardType = KeyboardType.Password))
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = primarycolor
    ){ innerPadding ->
        Box(modifier = Modifier.fillMaxSize()){
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if(Firebase.auth.currentUser != null) "Completar registro" else "Registrarse",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = RobotoBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StepIndicator(stepNumber = 1, isActive = false)
                    Spacer(modifier = Modifier.width(50.dp))
                    StepIndicator(stepNumber = 2, isActive = true)
                    Spacer(modifier = Modifier.width(50.dp))
                    StepIndicator(stepNumber = 3, isActive = false)
                }

                Spacer(modifier = Modifier.height(24.dp))

                DocumentTypeDropdown(
                    query = state.documentTypeQuery,
                    onQueryChanged = viewModel::onDocumentQueryChanged,
                    expanded = state.isDropdownExpanded,
                    onFocusChanged = viewModel::onDropdownFocusChanged,
                    documentTypes = state.documentTypes,
                    onDocumentTypeSelected =  { docType ->
                    viewModel.onDocumentTypeSelected(docType)
                    // Mueve el foco al siguiente TextField automáticamente
                    focusManager.moveFocus(FocusDirection.Down)
                },
                    errorMessage = state.documentTypeError
                )

                Spacer(modifier = Modifier.height(10.dp))

                formFields.forEach { field ->
                    when (field.type) {
                        FormFieldType.Password -> {
                            RegisterPasswordField(
                                value = field.value,
                                onValueChange = field.onValueChange,
                                label = field.label,
                                errorMessage = field.errorMessage,
                                passwordVisible = passwordVisible,
                                onVisibilityChange = { passwordVisible = !passwordVisible },
                                keyboardOptions = field.keyBoardOptions
                            )
                        }
                        else -> {
                            RegisterTextField(
                                value = field.value,
                                onValueChange = field.onValueChange,
                                label = field.label,
                                errorMessage = field.errorMessage,
                                keyboardOptions = field.keyBoardOptions
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Button(
                        onClick = viewModel::onNextClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F7DFF))
                    ) {
                        Text("Siguiente", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = viewModel::onCancelClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF3F7DFF))
                    ) {
                        Text("Volver", color = Color(0xFF3F7DFF))
                    }
                }
            }
        }
    }

}


