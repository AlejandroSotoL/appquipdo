package com.tramites1cero1.tramiappquibdo.ui.screen.main.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import kotlinx.coroutines.launch

data class SideMenuState(
    val isDarkTheme: Boolean = false,
    val isActiveReminders: Boolean = false,
    val showChangeLocationDialog: Boolean = false,
    val currentMunicipalityName : String = "",
)

data class SideMenuActions(
    val onThemeToggle: () -> Unit,
    val onChangeLocationClick: () -> Unit,
    val onConfirmChangeLocation: () -> Unit,
    val onDismissDialog:() -> Unit,
    val goToSettingsUser:() -> Unit,
    val onSaveSelectionRemiders:(Boolean) -> Unit,
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainSideMenuOptions(
    state: SideMenuState,
    actions: SideMenuActions,
    authViewModel : AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
){
    val userInformation by authViewModel.user.collectAsState(initial = null)
    val _isError = remember { mutableStateOf<ValidationResponseDTO?>(null) }
    val isRemindersActive by mainViewModel.isActiveReminders.collectAsState()
    val sideMenuState = state.copy(
        isActiveReminders = isRemindersActive
    )
    if(state.showChangeLocationDialog) {
        AlertDialog(
            onDismissRequest = {
                actions.onDismissDialog
            },
            title = { Text("Confirmar cambio de municipio actual: ${state.currentMunicipalityName}", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Al aceptar serás redireccionado a la pantalla de Bienvenida para que selecciones tu nueva ubicación, " +
                    "¿Deseas continuar?", style = MaterialTheme.typography.bodyMedium)},
            confirmButton = {
                TextButton(
                    onClick = actions.onConfirmChangeLocation
                ) {
                    Text("Aceptar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            },
            dismissButton  = {
                TextButton(
                    onClick = actions.onDismissDialog
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            },
        )
    }
    LaunchedEffect(userInformation) {
        if (userInformation != null) {
            Log.d("LoginOptionsScreen", "Usuario actual: $userInformation")
        } else {
            Log.d("LoginOptionsScreen", "No hay usuario activo")
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ){
        Spacer(Modifier.height(12.dp))

        Text("Configuración", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Spacer(Modifier.height(5.dp))
        if (userInformation != null && userInformation?.loginStatus == true || FirebaseAuth.getInstance().currentUser != null) {
            userInfoDetails(authViewModel = authViewModel, actions = actions , state = sideMenuState , )
        }
        Spacer(Modifier.height(5.dp))

        Row(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tema Oscuro",
                style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = state.isDarkTheme,
                onCheckedChange = { actions.onThemeToggle() }
            )
        }

        Spacer(Modifier.height(5.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = { Text("Cambiar de alcaldía", style = MaterialTheme.typography.bodyMedium) },
            selected = false,
            icon = { Icon(Icons.Outlined.LocationOn, contentDescription = "Cambiar de alcaldía") },
            onClick = actions.onChangeLocationClick
        )
        NavigationDrawerItem(
            label = { Text("Ayuda",
                style = MaterialTheme.typography.bodyMedium) },
            selected = false,
            icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
            onClick = { /* Handle click */ },
        )
        Spacer(Modifier.height(12.dp))

        HorizontalDivider()

        _isError.value?.let { state ->
            Text(
                text = state.sentencesError.toString() ?: "N/A",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
        }
    }

@Composable
fun userInfoDetails(
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewmodel: MainViewModel = hiltViewModel(),
    actions: SideMenuActions,
    state: SideMenuState,
){
    val userInformation by authViewModel.user.collectAsState(initial = null)
    val _isError = remember { mutableStateOf<ValidationResponseDTO?>(null) }

    //Proxima configuracion
//    LaunchedEffect(Unit) {
//        authViewModel.loadAuthToken()
//    }
//
//    Text(text = authToken ?: "Cargando token...")


    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Hola!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${userInformation?.firstName?:""} ${userInformation?.lastName?:""}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    val id = userInformation?.id
                    if (id != null && id > 0) {
                        coroutineScope.launch {
                            val result = authViewModel.clearUserData(id, false)
                            mainViewmodel.onSaveSelectionRemiders(false)
                            _isError.value = result
                        }
                    } else {
                        _isError.value = ValidationResponseDTO(
                            booleanStatus = false,
                            sentencesError = "Tenemos problemas, intenta más tarde"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.Bold
                )
            }

            _isError.value?.let { state ->
                if (!state.booleanStatus) {
                    Text(
                        text = state.sentencesError ?: "N/A",
                        color = Color.Black
                    )
                    LaunchedEffect(state) {
                        val id = userInformation?.id
                        if (id != null && id > 0) {
                            val retry = authViewModel.clearUserData(id, false)
                            _isError.value = retry
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    actions.goToSettingsUser()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ir a Configurar")
            }

            Spacer(Modifier.height(5.dp))

            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Activar Recordatorios",
                    style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = state.isActiveReminders,
                    onCheckedChange = { checked ->
                        actions.onSaveSelectionRemiders(checked)
                    }
                )
            }
        }
    }
}
