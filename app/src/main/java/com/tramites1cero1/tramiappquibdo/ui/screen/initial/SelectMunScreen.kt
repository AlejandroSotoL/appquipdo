package com.tramites1cero1.tramiappquibdo.ui.screen.initial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.ui.components.FooterSponsors
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.components.SelectMunicipio
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthEvents
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.login.LoginEvent
import com.tramites1cero1.tramiappquibdo.ui.screen.login.LoginOptionsScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.login.LoginOptionsViewModel
import com.tramites1cero1.tramiappquibdo.ui.theme.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import com.tramites1cero1.tramiappquibdo.ui.theme.Blue
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray300
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray900
import com.tramites1cero1.tramiappquibdo.ui.theme.White
import com.tramites1cero1.tramiappquibdo.ui.theme.buttoncolorslogin
import com.tramites1cero1.tramiappquibdo.ui.theme.primarycolor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMunScreen(
    navController: NavController,
    municipalityViewModel: MunicipalityViewModel,
    authViewModel: AuthViewModel = hiltViewModel(),
    loginViewModel: LoginOptionsViewModel = hiltViewModel(),
    viewModel: SelectMunViewModel = hiltViewModel(),
    mainViewModel : MainViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val userInformation by authViewModel.user.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showLoginOptionsSheet by rememberSaveable { mutableStateOf(true) }
    var showButtonsignOut by rememberSaveable { mutableStateOf(false) }
    var showLoginFormSheet by rememberSaveable { mutableStateOf(false) }
    val filteredMunicipalities by viewModel.filteredMunicipalities.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current

    var showNullDialog by remember { mutableStateOf(false) }
    var messageNullDialog by remember { mutableStateOf("") }

    LaunchedEffect(userInformation) {
        userInformation?.let { info ->
            if (info.loginStatus) {
                showLoginOptionsSheet = false
            } else {
                authViewModel.clearUserData(info.id, false)
                messageNullDialog ="We’re having trouble verifying your login status. You must login again"
                showNullDialog = true
                showLoginOptionsSheet = true
            }
        }
    }

    if (showNullDialog) {
        AlertDialog(
            onDismissRequest = { showNullDialog = false },
            confirmButton = {
                TextButton(onClick = { showNullDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Login Issue") },
            text = { Text(messageNullDialog) }
        )
    }

    LaunchedEffect(Unit) {
        loginViewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.NavigateToEmailLogin -> {
                    showLoginFormSheet = true
                    showLoginOptionsSheet = false
                    showButtonsignOut = false
                }
                is LoginEvent.NavigateToRegister -> {
                    showLoginOptionsSheet = false
                    showButtonsignOut = false
                    navController.navigate(AppRoutes.SIGNUP_STEPONE)
                }
                is LoginEvent.ContinueAsGuest -> {
                    showLoginOptionsSheet = false
                    showButtonsignOut = false
                }
                is LoginEvent.StartGoogleLogin -> {
                    showLoginOptionsSheet = false
                    showButtonsignOut = false
                }

                is LoginEvent.RequestAdditionalPermissions -> TODO()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is SelectMunEvent.NavigateToMain -> {
                    navController.navigate(AppRoutes.MAIN_NAV_GRAPH) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
                is SelectMunEvent.OnBackClicked -> {
                    navController.popBackStack()
                }
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
            authViewModel.eventFlow.collect { event ->
                if (event == AuthEvents.GoBack) {
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
                    showLoginFormSheet = false
            })
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = Blue,
        checkmarkColor = Color.White,
        uncheckedColor = Color.Gray
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 80.dp, end = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    modifier = Modifier.size(50.dp),
                    onClick = {
                        if(userInformation != null && userInformation?.loginStatus == true || FirebaseAuth.getInstance().currentUser != null ){
                            showButtonsignOut = true
                        }else{
                            showLoginOptionsSheet = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Abrir sesión",
                        tint = buttoncolorslogin,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(50.dp)
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp, end = 26.dp, bottom = 70.dp, top = 150.dp)
            ) {
                FooterSponsors("", White)
                Spacer(modifier = Modifier.height(30.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut() + slideOutVertically()
                ){
                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            Text("Cancelar",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Gray900)
                        }
                        Button(
                            onClick = {
                                viewModel.onContinueClicked()
                                municipalityViewModel.loadMunicipalityData(
                                    state.selectedMunicipality?.id ?: 0
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).height(50.dp),
                            enabled = state.selectedMunicipality != null,
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            Text(
                                "Continuar",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = primarycolor
    ){
        innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        ){
            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(scrollState)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { -50 }),
                    exit = fadeOut() + slideOutVertically()
                ){
                    Image(
                        painter = painterResource(id = R.drawable.newtramiapp),
                        contentDescription = "Logo TrámiApp",
                        modifier = Modifier.size(width = 210.dp, height = 100.dp)
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { -50 }),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(
                        text = "Ahora, elige tu municipio de\nresidencia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    SelectMunicipio(
                        state = state,
                        municipiosFiltrados = filteredMunicipalities,
                        onQueryChanged = { viewModel.onQueryChanged(it) },
                        onFocusChanged = { viewModel.onFocusChanged(it) },
                        onMunicipioSelected = { viewModel.onMunicipalitySelected(it) },
                        visible = visible
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = state.guardarUbicacion,
                            onCheckedChange = { viewModel.onSaveUbicationChange(it) },
                            colors = checkboxColors
                        )
                        Text(
                            text = "Guardar selección para futuros accesos.",
                            color = White,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }

    if (showButtonsignOut) {
        ModalBottomSheet(
            onDismissRequest = { showButtonsignOut = false },
            sheetState = sheetState,
            containerColor = Gray300
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.nuevotramiapp),
                    contentDescription = "Logo TrámiApp",
                    modifier = Modifier.padding(40.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
                // Mensaje de bienvenida / despedida

                Text(
                    text = "${userInformation?.firstName} ${userInformation?.lastName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Cerrar sesión?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Botón para cerrar sesión
                Button(
                    onClick = {
                        loginViewModel.signOut()
                        val id = userInformation?.id
                        if (id != null && id > 0) {
                            coroutineScope.launch {
                                authViewModel.clearUserData(id, false)
                                    mainViewModel.onSaveSelectionRemiders(false)

                                navController.navigate(AppRoutes.INITIAL_NAV_GRAPH)
                            }

                        }
                        showButtonsignOut = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Black

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Cerrar sesión", color = Color.Black)
                }
            }

        }

    }
}

