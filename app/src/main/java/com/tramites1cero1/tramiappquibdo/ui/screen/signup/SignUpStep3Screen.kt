package com.tramites1cero1.tramiappquibdo.ui.screen.signup

import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.signup.components.CheckboxRow
import com.tramites1cero1.tramiappquibdo.ui.screen.signup.components.RegisterTextField
import com.tramites1cero1.tramiappquibdo.ui.screen.signup.components.StepIndicator
import com.tramites1cero1.tramiappquibdo.ui.theme.RobotoBold
import com.tramites1cero1.tramiappquibdo.ui.theme.White
import com.tramites1cero1.tramiappquibdo.ui.theme.primarycolor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpThreeScreen(
    navController: NavController,
    viewModel: SignUpStep3ViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    //Scanning To User.
    val scanningLogIn by viewModel.scanningLogIn
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLoginFormSheet by rememberSaveable { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SignUpStep3Event.NavigateBack -> navController.popBackStack()
                is SignUpStep3Event.RegistrationSuccess -> {
                    navController.navigate(AppRoutes.INITIAL_NAV_GRAPH) {
                        popUpTo(AppRoutes.SIGNUP_NAV_GRAPH) { inclusive = true }
                    }
                }

                else -> {}
            }
        }
    }

    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = viewModel::onDatePickerDismissed,
            confirmButton = {
                Button(onClick = {
                    // Si el usuario selecciona una fecha, la procesamos
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onDateSelected(it)
                    }
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = viewModel::onDatePickerDismissed) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
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
                    .verticalScroll(scroll)
                    .imePadding()

            ) {
                IconButton(onClick = viewModel::onBackClicked) {
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
                    StepIndicator(stepNumber = 2, isActive = false)
                    Spacer(modifier = Modifier.width(50.dp))
                    StepIndicator(stepNumber = 3, isActive = true)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Teléfono
                RegisterTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChanged,
                    label = "Teléfono",
                    errorMessage = state.phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )

                // Fecha de nacimiento
                RegisterTextField(
                    value = state.birthDate,
                    onValueChange = { viewModel.onBirthDateInputClicked() },
                    label = "Fecha de nacimiento (DD/MM/AAAA)",
                    errorMessage = state.birthDateError
                )

                // Dirección
                RegisterTextField(
                    value = state.address,
                    onValueChange = { viewModel.onAddressChanged(it) },
                    label = "Dirección",
                    errorMessage = state.addressError,
                )

                Spacer(modifier = Modifier.height(16.dp))

                CheckboxRow(
                    text = "Acepto las políticas de tratamiento de datos",
                    checked = state.hasAcceptedPrivacyPolicy,
                    onCheckedChange = viewModel::onPrivacyPolicyAccepted
                )

                CheckboxRow(
                    text = "Acepto las condiciones de uso y las políticas de privacidad",
                    checked = state.hasAcceptedTerms,
                    onCheckedChange = viewModel::onTermsAccepted
                )

                if (state.termsError != null) {
                    Text(
                        text = state.termsError ?: "Acepta todos los términos y políticas",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                val context = LocalContext.current

                scanningLogIn?.let { info ->
                    Text(
                        text = info.sentencesError ?: "Sin error",
                        color = Color.White
                    )

                    LaunchedEffect(key1 = info) {
                        Toast.makeText(
                            context,
                            "Inicio de sesión satisfactorio. Te redirigimos al inicio de sesión.",
                            Toast.LENGTH_LONG
                        ).show()
                        delay(1500)
                        if(FirebaseAuth.getInstance().currentUser != null){
                            showLoginFormSheet = false
                        }else{
                            showLoginFormSheet = true
                        }
                    }
                } ?: Text(
                    text = "No tiene información",
                    color = Color.White
                )

                if (showLoginFormSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showLoginFormSheet = false },
                        sheetState = sheetState,
                        containerColor = primarycolor
                    ) {
                        AuthScreen(
                            navController = navController,
                            onLoginSuccess = {
                                showLoginFormSheet = false
                                navController.navigate(AppRoutes.INITIAL_NAV_GRAPH)
                            }
                        )
                    }
                }
                Button(
                    onClick = {
                        viewModel.onFinishClicked()
                        if (FirebaseAuth.getInstance().currentUser != null) {
                            navController.navigate(AppRoutes.INITIAL_NAV_GRAPH)
                        }
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4364CD))
                ) {
                    Text("Finalizar", color = Color.White)
                }
            }
        }
    }
}
