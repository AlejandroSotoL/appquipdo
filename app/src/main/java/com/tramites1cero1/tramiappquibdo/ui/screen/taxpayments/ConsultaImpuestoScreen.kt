package com.tramites1cero1.tramiappquibdo.ui.screen.taxpayments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.domain.model.Design
import com.tramites1cero1.tramiappquibdo.domain.model.Tax
import com.tramites1cero1.tramiappquibdo.ui.components.PolicyCheckboxes
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.MainSideMenuOptions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.SideMenuActions
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.SideMenuState
import com.tramites1cero1.tramiappquibdo.ui.screen.taxpayments.components.BottomSheetOpciones
import com.tramites1cero1.tramiappquibdo.ui.screen.taxpayments.components.CustomAnimatedDropdownMenu
import com.tramites1cero1.tramiappquibdo.ui.theme.TramitesSectionColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxQueryScreen(
    taxQueryviewModel: TaxQueryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    onCancel: () -> Unit,
    onQuerySuccess: (taxes: List<Tax>, email: String) -> Unit,
    navController: NavController,
    design: Design,
    departamento: String
) {

    val state by taxQueryviewModel.uiState.collectAsStateWithLifecycle()
    var showOptionsSheet by remember { mutableStateOf(false) }

    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current


    if (state.showNoResultsDialog) {
        NoResultsDialog(onDismiss = taxQueryviewModel::onNoResultsDialogDismissed)
    }

    LaunchedEffect(state.querySuccess) {
        state.querySuccess?.let { taxes ->
            if (taxes.isNotEmpty()) {
                onQuerySuccess(taxes, state.email)
                taxQueryviewModel.onQueryHandled()
            }
        }
    }

    if (showOptionsSheet) {
        BottomSheetOpciones(onDismissRequest = { showOptionsSheet = false })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                val sideMenuState = SideMenuState(
                    isDarkTheme = mainState.isDarkTheme,
                    isActiveReminders = mainState.isVisibleReminders,
                    showChangeLocationDialog = mainState.showChangeLocationDialog,
                    currentMunicipalityName = mainState.currentMunicipalityName ?: "Trami App",
                )
                val sideMenuActions = SideMenuActions(
                    onThemeToggle = mainViewModel::onThemeToggled,
                    onChangeLocationClick = mainViewModel::onChangeLocationClicked,
                    onConfirmChangeLocation = mainViewModel::onConfirmChangeLocation,
                    onDismissDialog = mainViewModel::onDismissChangeLocationDialog,
                    goToSettingsUser = mainViewModel::goToSettingUser,
                    onSaveSelectionRemiders = mainViewModel::onSaveSelectionRemiders
                )
                MainSideMenuOptions(
                    state = sideMenuState,
                    actions = sideMenuActions,
                    navController = navController,
                    design = design,
                    departamento = departamento
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(35.dp)
                                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(30.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    actions = {

                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },

            //floatingActionButton = {
            //    FloatingActionButton(onClick = { showOptionsSheet = true }) {
            //        Icon(Icons.Default.Settings, contentDescription = "Configuración")
            //    }
            //}

            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 50.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F4F4F)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "Cancelar",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = taxQueryviewModel::onQueryClicked,
                        enabled = state.isQueryButtonEnabled && !state.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Consultar", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusManager.clearFocus() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)

            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(TramitesSectionColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = state.taxIconResId),
                            contentDescription = "Icono impuesto",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Consulta tus facturas y haz el pago de tus impuestos de manera rápida y segura.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Justify
                    )
                }
                Column {

                    DropdownTriggerField(
                        displayText = state.selectedQueryField?.queryFieldType  ?: "",
                        placeholderText = "Selecciona tipo de documento",
                        isExpanded = state.isDropdownVisible,
                        onClick = { taxQueryviewModel.onDropdownVisibilityChanged(!state.isDropdownVisible) }
                    )

                    CustomAnimatedDropdownMenu(
                        isVisible = state.isDropdownVisible,
                        items = state.documentTypeOptions,
                        onItemSelected = taxQueryviewModel::onDocumentTypeChanged,
                        itemToString = { it.queryFieldType  }
                    )
                }


                CustomTextField(
                    value = state.documentNumber,
                    onValueChange = taxQueryviewModel::onDocumentNumberChanged,
                    label = {
                        Text(
                            "Número de documento",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )

                CustomTextField(
                    value = state.email,
                    onValueChange = taxQueryviewModel::onEmailChanged,
                    label = {
                        Text(
                            "Correo electrónico",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )

                PolicyCheckboxes(
                    dataPolicyChecked = state.acceptsPolicies,
                    onDataPolicyChange = taxQueryviewModel::onAcceptsPoliciesChanged,
                    privacyPolicyChecked = state.acceptsConditions,
                    onPrivacyPolicyChange = taxQueryviewModel::onAcceptsConditionsChanged,
                    dataPolicyUrl = state.dataPolicyUrl,
                    privacyPolicyUrl = state.privacyPolicyUrl
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun DropdownTriggerField(
    displayText: String,
    placeholderText: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = if (displayText.isEmpty()) placeholderText else displayText,
            style = MaterialTheme.typography.bodySmall,
            color = if (displayText.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
        )


        Icon(
            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
            contentDescription = "Desplegar menú",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = modifier
            .fillMaxWidth()

    )
}

@Composable
private fun NoResultsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        title = { Text("Consulta sin Resultados") },
        text = { Text("No se encontraron facturas o impuestos asociados a los datos ingresados. Por favor, verifique la información e intente de nuevo.") }
    )
}
