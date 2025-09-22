package com.tramites1cero1.tramiappquibdo.ui.screen.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.ui.theme.Black
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray900
import com.tramites1cero1.tramiappquibdo.ui.theme.RobotoBold
import com.tramites1cero1.tramiappquibdo.ui.theme.buttoncolorsOptionScreen
import kotlinx.coroutines.launch
import com.tramites1cero1.tramiappquibdo.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginOptionsScreen(
    loginOptionsViewModel: LoginOptionsViewModel = hiltViewModel()
) {
    val loading by loginOptionsViewModel.loading.collectAsState()
    val message by loginOptionsViewModel.message.collectAsState()
    val context = LocalContext.current


    // Lanzador de Google SignIn
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginOptionsViewModel.viewModelScope.launch {
            loginOptionsViewModel.handleSignInResult(result.data)
        }
    }

    // Eventos UI
    LaunchedEffect(Unit) {
        loginOptionsViewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.NavigateToRegister -> {


                }
                is LoginEvent.ContinueAsGuest -> {
                    // navegar al home
                }
                is LoginEvent.NavigateToEmailLogin -> {
                    if (message != null) {
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is LoginEvent.StartGoogleLogin -> {
                    // reiniciar flujo de login con Google si se cerró sesión
                }
                is LoginEvent.RequestAdditionalPermissions -> {
                    // manejar permisos adicionales
                }
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.nuevotramiapp),
            contentDescription = "Logo TrámiApp",
            modifier = Modifier.padding(40.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        // Botón login con email
        Button(
            onClick = { loginOptionsViewModel.onEmailLoginClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttoncolorsOptionScreen)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(22.dp)
                )
                Text(
                    "Iniciar sesión",
                    fontSize = 15.sp,
                    fontFamily = RobotoBold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Botón login con Google
        Button(
            onClick = {
                launcher.launch(loginOptionsViewModel.googleSignInClient.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(25.dp)),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth()
                ) {
                Image(
                    painter = painterResource(id = R.drawable.icongoogle),
                    contentDescription = "Google",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(22.dp)
                )
                Text(
                    "Continuar con Google",
                    fontSize = 15.sp,
                    fontFamily = RobotoBold,
                    color = Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (loading) {
            Spacer(modifier = Modifier.height(10.dp))
            CircularProgressIndicator(color = Color.Black)
        }

        Text("ó", color = Color.Black)

        // Continuar como invitado
        OutlinedButton(
            onClick = { loginOptionsViewModel.onContinueAsGuestClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.dp, Black),
        ) {
            Text(
                "Continuar sin una cuenta",
                fontSize = 15.sp,
                fontFamily = RobotoBold,
                color = Color.Black,

            )
        }


        // Registro
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "¿No tienes una cuenta?",
                fontSize = 13.sp,
                fontFamily = RobotoBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = { loginOptionsViewModel.onRegisterClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttoncolorsOptionScreen)
            ) {
                Text(
                    "Registrarse",
                    fontSize = 15.sp,
                    fontFamily = RobotoBold,
                    color = Color.White
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.height(30.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    painter = painterResource(R.drawable.ico101software),
                    contentDescription = "Logo Software",
                    tint = Gray900,
                    modifier = Modifier.size(105.dp)
                )
            }
        }
    }
}
