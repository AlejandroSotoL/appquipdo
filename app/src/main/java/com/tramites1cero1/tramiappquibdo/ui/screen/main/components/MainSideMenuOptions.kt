package com.tramites1cero1.tramiappquibdo.ui.screen.main.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.domain.model.Design
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthEvents
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.login.LoginEvent
import com.tramites1cero1.tramiappquibdo.ui.screen.login.LoginOptionsScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.login.LoginOptionsViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray300
import com.tramites1cero1.tramiappquibdo.ui.theme.Roboto_medium
import com.tramites1cero1.tramiappquibdo.ui.theme.Roboto_regular
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun MainSideMenuOptions(
    state: SideMenuState,
    actions: SideMenuActions,
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    loginViewModel: LoginOptionsViewModel = hiltViewModel(),
    mainViewmodel: MainViewModel = hiltViewModel(),
    navController: NavController,
    design: Design,
    departamento: String? = null
){
    var showLoginFormSheet by rememberSaveable { mutableStateOf(false) }
    var showLoginOptionsSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

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
        userInformation?.let { info ->
            if (info.loginStatus) {
                showLoginOptionsSheet = false
            } else {
                authViewModel.clearUserData(info.id , false);
                showLoginOptionsSheet = true
            }
        }
    }
    LaunchedEffect(Unit) {
        loginViewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.NavigateToEmailLogin -> {
                    showLoginOptionsSheet = false
                    showLoginFormSheet = true
                }
                is LoginEvent.NavigateToRegister -> {
                    showLoginOptionsSheet = false
                    navController.navigate(AppRoutes.SIGNUP_STEPONE)
                }
                is LoginEvent.ContinueAsGuest -> {
                    showLoginOptionsSheet = false
                }
                is LoginEvent.StartGoogleLogin -> {
                    showLoginOptionsSheet = false
                }
                is LoginEvent.RequestAdditionalPermissions -> TODO()
            }
        }
    }

    if (showLoginOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLoginOptionsSheet = false },
            sheetState = sheetState,
            containerColor = Gray300
        ) {
            LoginOptionsScreen(loginOptionsViewModel = loginViewModel)
        }
    }

    if (showLoginFormSheet) {
        LaunchedEffect(Unit) {
            authViewModel.eventFlow.collect { event->
                if(event == AuthEvents.GoBack)  {
                    showLoginOptionsSheet = true
                    showLoginFormSheet = false
                }
            }
        }
        ModalBottomSheet(
            onDismissRequest = { showLoginFormSheet = false },
            sheetState = sheetState,
            containerColor = Gray300
        ) {
            AuthScreen(
                navController = navController,
                onLoginSuccess = {

                })
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
    ){
        Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(Modifier.height(50.dp))

            AsyncImage(
                model = design.escudoUrl,
                contentDescription = "Escudo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(8.dp)
            )
            Text(
                text = design.NombreAlcaldia,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 28.sp,
                fontFamily = Roboto_medium,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            Text(
                text = "Departamento de ${departamento}",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontFamily = Roboto_regular,
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )
            if (userInformation != null && userInformation?.loginStatus == true || FirebaseAuth.getInstance().currentUser != null) {

                Text(
                    text = "Hola, ${userInformation?.firstName ?: ""} ${userInformation?.lastName ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)){
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.DarkMode, contentDescription = "reminder")
                    Text("Tema Oscuro",
                        style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = state.isDarkTheme,
                    onCheckedChange = { actions.onThemeToggle() }
                )
            }
            if (userInformation != null && userInformation?.loginStatus == true || FirebaseAuth.getInstance().currentUser != null) {
                userInfoDetails(authViewModel = authViewModel, actions = actions , state = sideMenuState , )
            }
            NavigationDrawerItem(
                label = { Text("Ayuda",
                    style = MaterialTheme.typography.bodyMedium) },
                selected = false,
                icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
                onClick = { /* Handle click */ },
            )
            Spacer(Modifier.height(12.dp))

            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text("Términos y condiciones",
                    style = MaterialTheme.typography.bodyMedium) },
                selected = false,
                icon = { Icon(Icons.Default.ContentPaste, contentDescription = null) },
                onClick = { /* Handle click */ },
            )
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

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()


            if (userInformation == null) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {showLoginOptionsSheet = true},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Iniciar sesión")
                    }
                }
            }else{
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
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        }

    }

@Composable
fun userInfoDetails(
    authViewModel: AuthViewModel = hiltViewModel(),
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

            Spacer(Modifier.height(5.dp))
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.CircleNotifications, contentDescription = "reminder")
                    Text("Recordatorios",
                        style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = state.isActiveReminders,
                    onCheckedChange = { checked ->
                        actions.onSaveSelectionRemiders(checked)
                    }
                )
            }
            Spacer(Modifier.height(5.dp))
            HorizontalDivider()

            NavigationDrawerItem(
                label = { Text("Configuración",
                    style = MaterialTheme.typography.bodyMedium) },
                selected = false,
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                onClick = { actions.goToSettingsUser() },
            )
            Spacer(Modifier.height(5.dp))
            HorizontalDivider()

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




        }
    }
}
