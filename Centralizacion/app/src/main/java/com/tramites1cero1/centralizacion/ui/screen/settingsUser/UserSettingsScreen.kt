package com.tramites1cero1.centralizacion.ui.screen.settingsUser

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordRequestDto
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel
import com.tramites1cero1.centralizacion.ui.screen.main.RemindersViewModel
import com.tramites1cero1.centralizacion.ui.screen.settingsUser.components.ReusableModal
import com.tramites1cero1.centralizacion.ui.theme.ColorBoxModal
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.primarycolor
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserSettingsScreen(
    settingVm: UserSettingsViewModel = hiltViewModel(),
    authVm: AuthViewModel = hiltViewModel(),
) {
    var showActivatedRemindersByEmail by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showEditInfo by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val spacerSize = screenWidth / 10
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var ShowReutulizableModal by remember { mutableStateOf(false) }
    var ShowMessageDeleteAccount by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val user by authVm.user.collectAsState(initial = null)
    val isActiveSendEmail by settingVm.isActiveSendEmail.collectAsState()

    if (user == null || ShowMessageDeleteAccount) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray300),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    color = primarycolor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(60.dp)
                )

                if (ShowMessageDeleteAccount) {
                    Text(
                        text = "Estamos eliminando todos tus datos.\nPronto serás redireccionado",
                        color = primarycolor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .background(ColorBoxModal.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                }
            }
        }
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Configuración de tu cuenta",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(spacerSize))

            SettingItem(
                icon = Icons.Default.Lock,
                title = "Cambiar Contraseña",
                description = "Actualiza tu contraseña para mayor seguridad",
                backgroundColor = Color(0xFFFFCDD2),
                onClick = { showChangePassword = true }
            )

            SettingItem(
                icon = Icons.Default.Delete,
                title = "Eliminar Cuenta",
                description = "Esta acción no se puede deshacer",
                backgroundColor = Color(0xFFFFAB91),
                onClick = {
                    coroutineScope.launch {
                        ShowReutulizableModal = true
                    }
                }
            )

            SettingItem(
                icon = Icons.Default.Build,
                title = "Activar recordatorios por Correo Electronico",
                description = "Recordatorios",
                backgroundColor = Color(0xFFFFDC91),
                onClick = {
                    coroutineScope.launch {
                        showActivatedRemindersByEmail = true
                    }
                }
            );
        }

        if(showActivatedRemindersByEmail){
            ReusableModal(
                title = "ACTIVACIÓN DE RECORDATORIO POR CORREO",
                onDismiss = {showActivatedRemindersByEmail = false}
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Row(modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if(isActiveSendEmail) "Desactivar" else "Activar",
                            style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isActiveSendEmail,
                            onCheckedChange = { checked ->
                                settingVm.setSendEmail(checked)
                                Log.d("CAMBIADO" ,checked.toString())
                            }
                        )
                    }
                }
            }
        }

        if (ShowReutulizableModal) {
            ReusableModal(
                title = "ELIMINACIÓN DE CUENTA",
                onDismiss = { ShowReutulizableModal = false }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                user?.let { u ->
                                    try {
                                        isLoading = true
                                        val result = authVm.onDeleteUser(u.id)
                                        if (result.booleanStatus) {
                                            snackbarHostState.showSnackbar("Cuenta eliminada con éxito")
                                        } else {
                                            snackbarHostState.showSnackbar(result.sentencesError)
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Error inesperado: ${e.message}")
                                    } finally {
                                        isLoading = false
                                        ShowReutulizableModal = false
                                        authVm.clearuserPreferences()
                                        ShowMessageDeleteAccount = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminando cuenta...", color = Color.White)
                            }
                        } else {
                            Text("ELIMINAR CUENTA DEFINITIVAMENTE")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Gray300)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Recuerda que al eliminar tu cuenta se borrarán tus historiales de pago y configuraciones.",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }


        if (showChangePassword) {
            ReusableModal(
                title = "Cambiar Contraseña",
                onDismiss = { showChangePassword = false }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            currentPasswordError = if (it.isBlank()) "La contraseña actual no puede estar vacía" else ""
                        },
                        label = { Text("Contraseña actual") },
                        isError = currentPasswordError.isNotEmpty()
                    )
                    if (currentPasswordError.isNotEmpty()) Text(
                        currentPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newPasswordError = when {
                                it.isBlank() -> "La nueva contraseña no puede estar vacía"
                                it.length < 8 -> "Debe tener mínimo 8 caracteres"
                                else -> ""
                            }
                        },
                        label = { Text("Nueva contraseña") },
                        isError = newPasswordError.isNotEmpty()
                    )
                    if (newPasswordError.isNotEmpty()) Text(
                        newPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = when {
                                it.isBlank() -> "Debes confirmar la contraseña"
                                it != newPassword -> "Las contraseñas no coinciden"
                                else -> ""
                            }
                        },
                        label = { Text("Confirmar nueva contraseña") },
                        isError = confirmPasswordError.isNotEmpty()
                    )
                    if (confirmPasswordError.isNotEmpty()) Text(
                        confirmPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    val isFormValid = currentPasswordError.isEmpty() &&
                            newPasswordError.isEmpty() &&
                            confirmPasswordError.isEmpty() &&
                            currentPassword.isNotBlank() &&
                            newPassword.isNotBlank() &&
                            confirmPassword.isNotBlank()

                    Button(
                        onClick = {
                            if (newPassword == confirmPassword && newPassword.isNotBlank()) {
                                val structur = UpdatePasswordRequestDto(
                                    currentPassword = currentPassword,
                                    newPassword = newPassword
                                )
                                user?.let { currentUser ->
                                    coroutineScope.launch {
                                        isLoading = true
                                        val responsetoPassword = settingVm.createNewPassword(currentUser.id, structur)
                                        isLoading = false
                                        showChangePassword = false
                                        snackbarHostState.showSnackbar(responsetoPassword.sentencesError)
                                        newPassword = ""
                                        confirmPassword = ""
                                        currentPassword = ""
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Tenemos un problema con tus Contraseñas")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isFormValid && !isLoading,
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardando cambios...", color = Color.White)
                            }
                        } else {
                            Text("Guardar los Cambios")
                        }
                    }
                }
            }
        }

        if (showEditInfo) {
            ReusableModal(
                title = "Editar Información",
                onDismiss = { showEditInfo = false }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre") })
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Correo electrónico") })
                    Button(
                        onClick = {
                            showEditInfo = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Información actualizada")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }
                }
            }
        }
    }
}



@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(25.dp))
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}


