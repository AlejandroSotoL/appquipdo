package com.tramites1cero1.centralizacion.ui.screen.settingsUser.recoveryByForget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel
import com.tramites1cero1.centralizacion.ui.screen.settingsUser.UserSettingsViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordByForgetDto
import kotlinx.coroutines.launch
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.login.AuthScreen
import com.tramites1cero1.centralizacion.ui.theme.ColorTextPrimary
import com.tramites1cero1.centralizacion.ui.theme.ColorTextPrimaryVariant
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.Red
import com.tramites1cero1.centralizacion.ui.theme.White
import com.tramites1cero1.centralizacion.ui.theme.bottomSheetsColor
import com.tramites1cero1.centralizacion.ui.theme.buttoncolorslogin
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.style.TextAlign
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.primarycolor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeOnlyPasswordScreen(
    navController: NavController,
    settingVm: UserSettingsViewModel = hiltViewModel(),
    authVm: AuthViewModel = hiltViewModel()
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val user by authVm.user.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    var forceShowSheet by remember { mutableStateOf(false) }
    if (user == null && !forceShowSheet) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = buttoncolorslogin)
        }
        return
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )
    var showSheet by remember { mutableStateOf(false) }

    // UI principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Cambiar contraseña", textAlign = TextAlign.Center, modifier = Modifier.padding(start = 15.dp)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(White, shape = RoundedCornerShape(40.dp))
                            .size(35.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back",
                            tint = primarycolor,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primarycolor,
                    titleContentColor = White,
                    scrolledContainerColor = White
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 80.dp),
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
                    onClick = {
                        error = ""
                        when {
                            newPassword.isBlank() -> {
                                error = "El campo no puede estar vacío"
                            }
                            newPassword.length < 8 -> {
                                error = "Debe tener mínimo 8 caracteres"
                            }
                            !newPassword.any { it.isDigit() } -> {
                                error = "Debe incluir al menos un número"
                            }
                            !newPassword.any { it.isUpperCase() } -> {
                                error = "Debe incluir al menos una mayúscula"
                            }
                            !newPassword.any { it.isLowerCase() } -> {
                                error = "Debe incluir al menos una minúscula"
                            }
                            !newPassword.any { "!@#\$%^&+=¿?*._-".contains(it) } -> {
                                error = "Debe incluir al menos un carácter especial"
                            }
                            newPassword != confirmPassword -> {
                                error = "Las contraseñas no coinciden"
                            }
                            else -> {
                                user?.let { currentUser ->
                                    isLoading = true
                                    coroutineScope.launch {
                                        val response = settingVm.updatePasswordByForget(
                                            currentUser.id,
                                            request = UpdatePasswordByForgetDto(newPassword = newPassword)
                                        )
                                        if (response.booleanStatus) {
                                            authVm.clearUserData(currentUser.id, false)
                                            forceShowSheet = true
                                            showSheet = true
                                            newPassword = ""
                                            confirmPassword = ""
                                        }
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(160.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = White
                        )
                    } else {
                        Text("Finalizar", style = MaterialTheme.typography.headlineSmall)
                    }
                }

            }
        },
        containerColor = primarycolor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Ingresa tu nueva contraseña", style = MaterialTheme.typography.titleLarge, color = White)

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Contraseña", style = MaterialTheme.typography.bodyMedium, color = Gray300) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = White,
                    unfocusedTextColor = Gray300,
                    focusedIndicatorColor = buttoncolorslogin,
                    unfocusedIndicatorColor = Gray300,
                    cursorColor = buttoncolorslogin
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña", style = MaterialTheme.typography.bodyMedium, color = Gray300) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = White,
                    unfocusedTextColor = Gray300,
                    focusedIndicatorColor = buttoncolorslogin,
                    unfocusedIndicatorColor = Gray300,
                    cursorColor = buttoncolorslogin
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )


            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = Red)
            }

            Spacer(modifier = Modifier.height(15.dp))

        }
    }


    if (showSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest =  {
                coroutineScope.launch {
                    sheetState.show()
                }
            },
            containerColor = Gray300
        ) {
            AuthScreen(
                navController = navController,
                onLoginSuccess = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showSheet = false
                        navController.navigate(AppRoutes.INITIAL_NAV_GRAPH) {
                            popUpTo(AppRoutes.CONFIGURATIONS_USER_SCREEN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }

}

