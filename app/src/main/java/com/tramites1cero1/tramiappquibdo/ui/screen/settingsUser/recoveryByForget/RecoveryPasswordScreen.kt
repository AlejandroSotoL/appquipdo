package com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.recoveryByForget

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.ui.components.FooterSponsors
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.components.InfoText
import com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.components.ShowModalVerificationCode
import com.tramites1cero1.tramiappquibdo.ui.theme.*
import kotlinx.coroutines.launch
import com.tramites1cero1.tramiappquibdo.R
/* ---------- Helpers ---------- */
fun isValidEmail(email: String): Boolean =
    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryPasswordScreen(
    navController: NavController,
    recoveryPass: RecoveryPasswordViewModel = hiltViewModel()
) {
    val uiState by recoveryPass.uiState
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showWarningToast by remember { mutableStateOf(false) }

    /* --- Control del cierre del modal --- */
    LaunchedEffect(sheetState.currentValue) {
        snapshotFlow { sheetState.isVisible }
            .collect { visible ->
                if (!visible && uiState.showCodeSheet) {
                    recoveryPass.onModalCloseWithoutCompleting()
                    showWarningToast = true
                }
            }
    }

    if (showWarningToast) {
        LaunchedEffect(showWarningToast) {
            Toast.makeText(
                context,
                "No terminaste el proceso, cerrando sesión...",
                Toast.LENGTH_SHORT
            ).show()
            showWarningToast = false
        }
    }

    /* --- Abrir/cerrar modal según estado --- */
    LaunchedEffect(uiState.showCodeSheet) {
        if (uiState.showCodeSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    /* --- Navegación cuando es exitoso --- */
    LaunchedEffect(uiState.navigate) {
        if (uiState.navigate) {
            navController.navigate(AppRoutes.CHANGE_ONLY_PASSWORD)
            recoveryPass.resetNavigation()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo TrámiApp",
                    modifier = Modifier
                        .padding(top = 100.dp, bottom = 20.dp)
                        .size(140.dp)
                )
            }

        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FooterSponsors("", White)
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = primarycolor
    ){
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Ahora. Puedes recuperar tu contraseña,\nIngresa el correo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { recoveryPass.onEmailChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = { Text("Ingrese su correo.", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2196F3)
                    )
                )

                Spacer(modifier = Modifier.height(35.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.width(160.dp).height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Text("Cancelar",
                            style = MaterialTheme.typography.headlineSmall,)
                    }

                    Button(
                        onClick = { recoveryPass.recoveryPassword() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(160.dp).height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Text("Continuar",
                            style = MaterialTheme.typography.headlineSmall,)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoText(
                    message = uiState.sendCodeResponse?.sentencesError ?: uiState.errorMessage,
                    isSuccess = uiState.sendCodeResponse?.booleanStatus ?: true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.isCodeError) {
                    Text(
                        text = uiState.errorMessage ?: "Código inválido",
                        color = Red,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Te enviaremos un código de seguridad a tu dirección de correo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondaryVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if (sheetState.isVisible) {
            ModalBottomSheet(
                containerColor = Gray300,
                sheetState = sheetState,
                onDismissRequest = { coroutineScope.launch { sheetState.hide() } }
            ) {
                val isValida = !uiState.isCodeError
                ShowModalVerificationCode(  navController , isValida)
            }
        }
    }
}
