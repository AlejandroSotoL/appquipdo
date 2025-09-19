package com.tramites1cero1.tramiappquibdo.ui.screen.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.tramites1cero1.tramiappquibdo.MunicipalityUiState
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.domain.model.TramiteAccion
import com.tramites1cero1.tramiappquibdo.ui.components.FooterSponsors
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.BottomNavBar
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.BottomNavBarActions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.ConfirmExitDialog

import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.MainHeader
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.MainSideMenuOptions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.MainTopBar
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.ModalForm
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.RemindersSection
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.SideMenuActions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.SideMenuState
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.TopBarActions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.TramitesActions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.TramitesSection
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.TramitesState
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.PqrdsChoiceScreen
import com.tramites1cero1.tramiappquibdo.ui.theme.White
import com.tramites1cero1.tramiappquibdo.utils.abrirURL
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    munViewModel: MunicipalityViewModel,
    remindersViewModel: RemindersViewModel = hiltViewModel(),
) {
    val state by mainViewModel.uiState.collectAsStateWithLifecycle()
    val munState by munViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isRemindersActive by mainViewModel.isActiveReminders.collectAsState()


    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val activity = (context as? Activity)

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        val activity = context as? Activity
        activity?.let {
           // mainViewModel.launchInAppReview(it)
        }
        mainViewModel.getUserConfig()
        mainViewModel.loadBottomBarItems()
        mainViewModel.loadSavedMunicipality()
    }

//    LaunchedEffect(state.currentIdMunicipality, state.isSaved) {
//        if (state.isSaved && state.currentIdMunicipality != 0) {
//            munViewModel.loadMunicipalityData(state.currentIdMunicipality ?: 0)
//        }
//    }

    LaunchedEffect(currentRoute) {
        mainViewModel.onRouteChanged(currentRoute)
    }

    LaunchedEffect(Unit) {
        mainViewModel.event.collect { event ->
            when (event) {
                is MainEvent.Navigate -> navController.navigate(event.route)
                is MainEvent.OpenUrl -> {
                    val state = munState
                    val colorPrimario =
                        if (MunicipalityUiState.Success::class.java.isInstance(state)) {
                            (state as MunicipalityUiState.Success).data.design.primaryColor
                        } else {
                            Color.White
                        }
                    abrirURL(context, event.url, colorPrimario.toArgb())
                }

                is MainEvent.NavigateToWelcome -> {
                    navController.navigate(AppRoutes.INITIAL_NAV_GRAPH) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }

                is MainEvent.FinishApp -> activity?.finish()
                is MainEvent.OpenDrawer -> {
                    scope.launch {
                        drawerState.open()
                    }
                }

                is MainEvent.NavToPqrdAnonima -> {
                    navController.navigate(event.route)
                }

                is MainEvent.NavToPqrdIdentificacion -> {
                    navController.navigate(event.route)
                }
            }
        }
    }

    val sideMenuState = SideMenuState(
        isDarkTheme = state.isDarkTheme,
        isActiveReminders = state.isVisibleReminders,
        showChangeLocationDialog = state.showChangeLocationDialog,
        currentMunicipalityName = state.currentMunicipalityName ?: "Trami App",
    )

    val sideMenuActions = SideMenuActions(
        onThemeToggle = mainViewModel::onThemeToggled,
        onChangeLocationClick = mainViewModel::onChangeLocationClicked,
        onConfirmChangeLocation = mainViewModel::onConfirmChangeLocation,
        onDismissDialog = mainViewModel::onDismissChangeLocationDialog,
        goToSettingsUser = mainViewModel::goToSettingUser,
        onSaveSelectionRemiders = mainViewModel::onSaveSelectionRemiders
    )


    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            remindersViewModel.event.collect { event ->
                when (event) {
                    is ReminderEvent.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }

                    is ReminderEvent.RequestExactAlarmPermission -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                                context.startActivity(it)
                            }
                        }
                    }
                }
            }
        }
    }

    BackHandler(enabled = true) {
        if (state.isPqrdVisible) {
            mainViewModel.onPqrdsCancel()
        } else {
            mainViewModel.onBackPressed()
        }
    }

    when (val munState = munState) {
        is MunicipalityUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is MunicipalityUiState.Success -> {
            val municipalityData = munState.data
            val tramitesPrincipales = munState.tramitesPrincipales
            val otrosTramites = munState.otrosTramites
            val onlyprocedures = munState.onlyProcedures
            val bank = munState.data.bank
            val socialLinks = municipalityData.socialLinks

            if (state.showInDevelopmentDialog) {
                InDevelopmentDialog(
                    onDismiss = mainViewModel::onInDevelopmentDialogDismiss
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.circles),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(110.dp)
                        .offset(x = (35).dp, y = (-30).dp)
                        .zIndex(2f),
                    colorFilter = ColorFilter.tint(White.copy(alpha = 0.2f))
                )
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(windowInsets = WindowInsets(0)) {
                            MainSideMenuOptions(
                                state = sideMenuState,
                                actions = sideMenuActions,
                                navController = navController,
                                design = municipalityData.design,
                                departamento = municipalityData.departamento
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = { MainTopBar(actions = TopBarActions(
                            onMenuClicked = mainViewModel::onMenuClicked,
                            onBackClicked = mainViewModel::onBackPressed)) },
                        bottomBar = {
                            val navBarActions = BottomNavBarActions(
                                onNavItemClicked = { navItem ->
                                    mainViewModel.onNavItemClicked(navItem, municipalityData)
                                }
                            )
                            BottomNavBar(
                                state = state.navBarState,
                                actions = navBarActions
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            MainHeader(
                                design = municipalityData.design
                            , departamento = municipalityData.departamento)

                            if (!state.isPqrdVisible) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 180.dp)
                                        .clip(
                                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                        .background(MaterialTheme.colorScheme.background),
                                    contentPadding = PaddingValues(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 10.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    item {
                                        if (isRemindersActive) {
                                            AnimatedSection {
                                                RemindersSection(onlyprocedures)
                                            }
                                        }
                                    }
                                    item {
                                        AnimatedSection {
                                            val tramitesPrincipalesState = TramitesState(
                                                titulo = "Trámites",
                                                tramites = tramitesPrincipales,
                                                searchText = state.searchText,
                                                isSearchActive = state.isSearchActive,
                                                isSearchable = true
                                            )

                                            val tramitesActions = TramitesActions(
                                                onSearchTextChanged = mainViewModel::onSearchTextChanged,
                                                onSearchToggled = mainViewModel::onSearchToggled,
                                                onTramiteClick = mainViewModel::onTramiteClicked
                                            )
                                            TramitesSection(
                                                state = tramitesPrincipalesState,
                                                actions = tramitesActions
                                            )
                                        }
                                    }
                                    if (otrosTramites.isNotEmpty()) {
                                        item {
                                            AnimatedSection(delayMillis = 300) {
                                                val tramitesPrincipalesState = TramitesState(
                                                    titulo = "Otros trámites",
                                                    tramites = otrosTramites,
                                                    searchText = state.searchText,
                                                    isSearchActive = false,
                                                    isSearchable = false
                                                )

                                                val tramitesActions = TramitesActions(
                                                    onSearchTextChanged = mainViewModel::onSearchTextChanged,
                                                    onSearchToggled = mainViewModel::onSearchToggled,
                                                    onTramiteClick = { tramite ->
                                                        if (tramite.accion is TramiteAccion.AbrirBotonPanico) {
                                                            locationPermissionState.launchPermissionRequest()
                                                        }
                                                        mainViewModel.onTramiteClicked(tramite)
                                                    }
                                                )
                                                TramitesSection(
                                                    state = tramitesPrincipalesState,
                                                    actions = tramitesActions
                                                )
                                            }
                                        }
                                    }
                                    if (socialLinks.isNotEmpty()) {
                                        item {
                                            AnimatedSection(delayMillis = 400) {
                                                val socialLinksState = TramitesState(
                                                    titulo = "Canales",
                                                    tramites = socialLinks,
                                                    searchText = state.searchText,
                                                    isSearchActive = false,
                                                    isSearchable = false
                                                )
                                                val tramitesActions = TramitesActions(
                                                    onSearchTextChanged = mainViewModel::onSearchTextChanged,
                                                    onSearchToggled = mainViewModel::onSearchToggled,
                                                    onTramiteClick = mainViewModel::onTramiteClicked
                                                )
                                                TramitesSection(
                                                    state = socialLinksState,
                                                    actions = tramitesActions
                                                )
                                            }
                                        }
                                    }
                                    item {
                                        AnimatedSection(delayMillis = 100) {
                                            FooterSponsors(
                                                bank,
                                                MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }
                            } else {
                                PqrdsChoiceScreen(
                                    mainViewModel = mainViewModel,
                                    codigoEntidad = municipalityData.codigoEntidad
                                )
                            }
                        }
                    }
                }
            }
            if (state.modalMode != null) {
                ModalForm(
                    onDismissRequest = mainViewModel::onModalDismissed,
                    onConfirm = mainViewModel::onModalConfirmed,
                    mode = state.modalMode!!,
                    dataPolicyUrl = municipalityData.dataPolicyUrl?:"",
                    privacyPolicyUrl = municipalityData.privacyPolicyUrl?: ""
                )
            }
            // Opcional: Muestra un indicador de carga mientras se obtienen los datos iniciales.
            if (state.showExitDialog) {
                ConfirmExitDialog(
                    onDismissRequest = mainViewModel::onExitDialogDismissed,
                    onConfirm = mainViewModel::onConfirmExit
                )
            }
        }

        is MunicipalityUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error al cargar los datos del municipio.")

            }
        }

        is MunicipalityUiState.Empty -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

            }
        }
    }

}


@Composable
fun AnimatedSection(
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "alphaAnim"
    )

    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 10.dp,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "offsetAnim"
    )
    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = offsetY.toPx()
            }
    ) {
        content()
    }
}

@Composable
private fun InDevelopmentDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        },
        title = { Text("Función en Desarrollo") },
        text = { Text("Esta opción se encuentra actualmente en desarrollo y estará disponible próximamente.") }
    )
}





