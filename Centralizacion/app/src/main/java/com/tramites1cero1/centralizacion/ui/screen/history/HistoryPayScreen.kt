package com.tramites1cero1.centralizacion.ui.screen.history

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.ui.components.FooterSponsors
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.login.AuthScreen
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel
import com.tramites1cero1.centralizacion.ui.screen.login.LoginEvent
import com.tramites1cero1.centralizacion.ui.screen.login.LoginOptionsScreen
import com.tramites1cero1.centralizacion.ui.screen.login.LoginOptionsViewModel
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.primarycolor
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPayScreen(
    navController: NavController,
    bank: String,
    authViewModel: AuthViewModel = hiltViewModel(),
    loginViewModel: LoginOptionsViewModel = hiltViewModel(),
    historyViewModel: HistoryPayViewModel = hiltViewModel(),
) {
    // State from VM
    val isStatusError by historyViewModel.isErrorExistedStatus
    val history by historyViewModel.historyPaymentsByUser.collectAsStateWithLifecycle()
    val isError by historyViewModel.isErrorExisted
    val userInformation by authViewModel.user.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val statuses by historyViewModel.informationStatus.collectAsStateWithLifecycle()
    val uiMessages = historyViewModel.uiMessages

    LaunchedEffect(userInformation?.id) {
        userInformation?.id?.let { idUser ->
            Log.d("HistoryUI", "Solicitando historial para userId=$idUser")
            historyViewModel.bringInformationByRepository(idUser)
        }
    }
    LaunchedEffect(history.size) {
        if (history.isEmpty()) {
            Log.d("HistoryUI", "No hay registros de historial")
        } else {
            Log.d("HistoryUI", "Llegaron ${history.size} registros, consultando estados...")
        }
    }

    LaunchedEffect(isStatusError) {
        isStatusError?.let {
            snackbarHostState.showSnackbar(it.sentencesError ?: "Error desconocido")
        }
    }
    LaunchedEffect(isError) {
        if (isError?.booleanStatus == false) {
            snackbarHostState.showSnackbar(isError?.sentencesError ?: "Ocurrió un error")
        }
    }

    var isOpenOpcLogin by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLoginFormSheet by rememberSaveable { mutableStateOf(false) }
    var showValidationSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loginViewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.NavigateToEmailLogin -> {
                    isOpenOpcLogin = false
                    showLoginFormSheet = true
                }
                is LoginEvent.NavigateToRegister -> {
                    isOpenOpcLogin = false
                    navController.navigate(AppRoutes.SIGNUP_STEPONE)
                }
                is LoginEvent.ContinueAsGuest -> { isOpenOpcLogin = false }
                is LoginEvent.StartGoogleLogin -> { isOpenOpcLogin = false }
                is LoginEvent.RequestAdditionalPermissions -> TODO()
            }
        }
    }

    if (isOpenOpcLogin) {
        ModalBottomSheet(
            onDismissRequest = { isOpenOpcLogin = false },
            sheetState = sheetState,
            containerColor = Gray300
        ) { LoginOptionsScreen() }
    }
    if (showLoginFormSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLoginFormSheet = false },
            sheetState = sheetState,
            containerColor = Gray300
        ) {
            AuthScreen(navController = navController, onLoginSuccess = { showValidationSheet = true })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.tramites1cero1.centralizacion.R.drawable.circles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
                .rotate(90f)
                .offset(x = (-25).dp, y = (-30).dp)
                .zIndex(1f),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary.copy(0.7f))
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Historial de pagos", textAlign = TextAlign.Start, modifier = Modifier.padding(start = 15.dp), color = MaterialTheme.colorScheme.secondary) },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(40.dp))
                                .size(35.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                val isLoginOrNot = userInformation?.loginStatus != true

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(25.dp))
                        .padding(11.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Mis Pagos",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        if (isLoginOrNot) {
                            SessionCheckMessage(openLoginOptions = { isOpenOpcLogin = true })
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(1),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
                            ) {
                                if (history.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No tienes aún pagos..",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                } else {
                                    items(history, key = { it.id }) { servicio ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            elevation = CardDefaults.cardElevation(4.dp),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = servicio.procedureName.takeIf { it.isNotBlank() } ?: "Cargando...",
                                                    style = MaterialTheme.typography.headlineMedium,
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))
                                                val status = statuses[servicio.id]
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Text("👤 ${servicio.userFirtName.takeIf { it.isNotBlank() } ?: "Cargando..."}", style = MaterialTheme.typography.bodyMedium)
                                                        Text(
                                                            text = "💰 ${if (servicio.amount > 0) "$${String.format("%,.2f", servicio.amount)}" else "Cargando..."}",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        Text("📅 ${servicio.paymentDate.takeIf { it.isNotBlank() } ?: "Cargando..."}", style = MaterialTheme.typography.bodyMedium)

                                                    }

                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        val (icono, color) = when (status?.EstadoTransaccion) {
                                                            "COMENZADA" -> Icons.Filled.HourglassEmpty to Color(0xFFFFC107)
                                                            "APROBADA" -> Icons.Filled.CheckCircle to Color(0xFF4CAF50)
                                                            "FALLIDA" -> Icons.Filled.Cancel to Color(0xFFF44336)
                                                            else -> Icons.Filled.HelpOutline to Color.Gray
                                                        }
                                                        Icon(
                                                            imageVector = icono,
                                                            contentDescription = "Estado",
                                                            tint = color,
                                                            modifier = Modifier.size(36.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = status?.EstadoTransaccion  ?: "Cargando...",
                                                            color = color,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            FooterSponsors(bank, MaterialTheme.colorScheme.onBackground)
                                            HistoryLogBar(messageState = uiMessages)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryLogBar(messageState: StateFlow<HistoryPayViewModel.UiMessage?>) {
    val message by messageState.collectAsState()

    message?.let { uiMessage ->
        val (bgColor, contentColor, icon) = when (uiMessage) {
            is HistoryPayViewModel.UiMessage.Success -> Triple(Color(0xFFC8E6C9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
            is HistoryPayViewModel.UiMessage.Error -> Triple(Color(0xFFFFCDD2), Color(0xFFD32F2F), Icons.Default.Error)
            is HistoryPayViewModel.UiMessage.Warning -> Triple(Color(0xFFFFF9C4), Color(0xFFF57F17), Icons.Default.Warning)
            is HistoryPayViewModel.UiMessage.Info -> Triple(Color(0xFFBBDEFB), Color(0xFF1565C0), Icons.Default.Info)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiMessage.text,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



@Composable
fun SessionCheckMessage(openLoginOptions: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Outlined.Cancel,
                    contentDescription = "Sesión expirada",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(38.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Por favor, inicia sesión para ver tu historial.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = openLoginOptions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Iniciar sesión")
            }
        }
    }
}
