package com.tramites1cero1.tramiappquibdo.ui.screen.taxpayments

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tramites1cero1.tramiappquibdo.MunicipalityUiState
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.domain.model.Tax
import com.tramites1cero1.tramiappquibdo.ui.screen.taxpayments.components.TaxCard
import com.tramites1cero1.tramiappquibdo.ui.theme.White
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxResultsScreen(
    viewModel: TaxResultsViewModel = hiltViewModel(),
    munViewModel: MunicipalityViewModel = hiltViewModel(),
    taxes: List<Tax>,
    userEmail: String,
    onBack: () -> Unit,
    onGoToMain: () -> Unit,
    openUrl: (String) -> Unit
) {
    // --- Estados y datos ---
    val municipalityId by viewModel.municipalityId.collectAsStateWithLifecycle()
    val munState by munViewModel.uiState.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val validationCreatePaymentHistory by viewModel.validationPostCreate.collectAsState()
    val context = LocalContext.current

    if (state.isLoading) {
        DownloadingDialog()
    }

    val shouldShowHomeButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0 &&
                    state.taxes.size >= 2
        }
    }

    // --- Inicialización ---
    LaunchedEffect(municipalityId) {
        viewModel.setTaxes(taxes)
        if (municipalityId != 0) {
            munViewModel.loadMunicipalityData(municipalityId)
        }
    }

    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            openUrl(url)
            viewModel.onNavigationHandled()
        }
    }

    LaunchedEffect(state.fileToOpenUri) {
        state.fileToOpenUri?.let { uri ->
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(intent, "Abrir PDF con...")
                context.startActivity(chooser)
            } catch (e: Exception) {
                Toast.makeText(context, "No se encontró una aplicación para abrir el PDF.", Toast.LENGTH_LONG).show()
            } finally {
                viewModel.onFileActionHandled()
            }
        }
    }

    LaunchedEffect(state.fileToShareUri) {
        state.fileToShareUri?.let { uri ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
            viewModel.onFileActionHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Facturas Encontradas",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = { Spacer(modifier = Modifier.width(68.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            when (val munInformation = munState) {
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
                    // aquí podrías renderizar info del municipio si lo necesitas
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
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    items = state.taxes,
                    key = { _, tax -> tax.reference }
                ) { index, tax ->

                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(500)) +
                                slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = tween(500)
                                )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            TaxCard(
                                tax = tax,
                                onPayClick = { viewModel.onPayClicked(it, userEmail) },
                                onPdfClick = { viewModel.onOpenPdfClicked(it) },
                                onShareClick = { viewModel.onSharePdfClicked(it) },
                                onRegiserPayment = {
                                    if (munState is MunicipalityUiState.Success) {
                                        val munInformation = munState as MunicipalityUiState.Success
                                        val matchedProcedure = munInformation.onlyProcedures
                                            .firstOrNull { it.procedures.id == tax.taxId }
                                        if (matchedProcedure != null) {
                                            var isValidData = false
                                            val procedureId = matchedProcedure.id
                                            if (
                                                !procedureId.toString().isNullOrEmpty() &&
                                                !tax.value.toString().isNullOrEmpty() &&
                                                !tax.taxId.toString().isNullOrEmpty() &&
                                                !tax.invoice.isNullOrEmpty() &&
                                                !tax.entityCode.isNullOrEmpty()
                                            ) {
                                                isValidData = true
                                            }
                                            if (isValidData) {
                                                viewModel.createHistoryPay(
                                                    amount = tax.value.toFloat(),
                                                    IDImpuesto = tax.taxId.toString(),
                                                    factura = tax.invoice,
                                                    codigoEntidad = tax.entityCode,
                                                    municipalityProceduresId = procedureId
                                                )
                                            } else {
                                                Toast.makeText(context, "Algunos de los datos de tu información tiene problemas, Intenta mas tarde", Toast.LENGTH_LONG).show()
                                                return@TaxCard
                                            }
                                        } else {
                                            Toast.makeText(context, "Tenemos problemas con tu Alcadia, Intenta mas tarde", Toast.LENGTH_LONG).show()
                                            return@TaxCard
                                        }
                                    } else {
                                        Toast.makeText(context, "Tenemos problemas con tu Alcadia, Intenta mas tarde", Toast.LENGTH_LONG).show()
                                        return@TaxCard
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // --- ALERTA ABAJO DEL TODO ---
            validationCreatePaymentHistory?.let { validation ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { it }),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    ValidationAlert(validation)
                }
            }
        }
    }
}


@Composable
fun ValidationAlert(validation: ValidationResponseDTO) {
    val backgroundColor: Color
    val icon = if (validation.booleanStatus) Icons.Default.CheckCircle else Icons.Default.Error
    val iconTint: Color
    val textColor: Color

    if (validation.booleanStatus) {
        backgroundColor = Color(0xFFE6F4EA)
        iconTint = Color(0xFF2E7D32)
        textColor = Color(0xFF2E7D32)
    } else {
        backgroundColor = Color(0xFFFFE5E5)
        iconTint = Color.Red
        textColor = Color.Red
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = if (validation.booleanStatus) "Éxito" else "Error",
                tint = iconTint
            )
            Text(
                text = validation.sentencesError,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DownloadingDialog() {
    Dialog(
        onDismissRequest = {  },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                Text(
                    text = "Descargando factura...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
