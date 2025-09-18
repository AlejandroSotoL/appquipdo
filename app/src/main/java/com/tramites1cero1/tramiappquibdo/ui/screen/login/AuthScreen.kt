package com.tramites1cero1.tramiappquibdo.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.ui.components.FooterSponsors
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.theme.Black
import com.tramites1cero1.tramiappquibdo.ui.theme.RobotoBold
import com.tramites1cero1.tramiappquibdo.ui.theme.buttoncolorsOptionScreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    onLoginSuccess: () -> Unit,
) {
    val state by authViewModel.uiState.collectAsState()
    var visible by remember { mutableStateOf(false) }


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        authViewModel.eventFlow.collect { event ->
            if(event == AuthEvents.OnLogin)  {
                onLoginSuccess()
            }
        }
    }

    val grayField = Color(0xFF3D3D4E)
    // UI Layout
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 5.dp, bottom = 35.dp, end = 16.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if(state.response != null){
            if (state.response!!.booleanStatus) {
                LaunchedEffect(Unit) {
                    email = ""
                    password = ""
                }
            }
            LaunchedEffect(Unit) {
                delay(2000)
                authViewModel.clearResponse()
            }
            AuthValidationScreen(state.response)
        }

        if(state.isLogginIn != true){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    IconButton(onClick = { authViewModel.GoBackToOptions() } ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier
                                .background(buttoncolorsOptionScreen, shape = CircleShape)
                                .size(48.dp)
                                .padding(8.dp)
                        )
                    }
                }

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.nuevotramiapp),
                    contentDescription = "Logo TrámiApp",
                    modifier = Modifier.padding(40.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Campo de Email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            "Correo electrónico",
                            fontFamily = RobotoBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = grayField,
                        unfocusedContainerColor = grayField,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                        errorIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Campo de Contraseña
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            "Contraseña",
                            fontFamily = RobotoBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null, tint = Color.White)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = grayField,
                        unfocusedContainerColor = grayField,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    )
                )

                TextButton(onClick = {
                    navController.navigate(AppRoutes.RECOVERY_PASSWORD)
                }) {
                    Text("¿Olvidaste tu contraseña?", fontSize = 13.sp, color = Color.Black)
                }
                Button(
                    onClick = {
                        authViewModel.authenticate(email, password)
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttoncolorsOptionScreen)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Iniciar sesión",
                            fontSize = 15.sp,
                            fontFamily = RobotoBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                FooterSponsors("",Black)
            }
        }
    }
}


