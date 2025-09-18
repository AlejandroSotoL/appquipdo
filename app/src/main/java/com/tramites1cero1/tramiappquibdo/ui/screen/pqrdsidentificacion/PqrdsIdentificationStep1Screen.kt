package com.tramites1cero1.tramiappquibdo.ui.screen.pqrdsidentificacion

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.ui.components.StepIndicator
import com.tramites1cero1.tramiappquibdo.ui.components.TopbarNavigation
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.PqrdsEvent
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.PqrdsViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.components.CustomDropdownPqrds
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray600


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PqrdsIdentificationNavScreen(
    navController: NavController,
    viewModel: PqrdsViewModel,
    codigoEntidad: String
){

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.loadDropdownData(codigoEntidad)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is PqrdsEvent.NavigateNextStep -> {
                    navController.navigate(event.route)
                }
                is PqrdsEvent.OnBackStep -> {
                    navController.popBackStack()
                }
                else -> {}
            }
        }
    }

    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    TopbarNavigation(
        onBackPressed = { if (state.currentStep == 1) navController.popBackStack() else viewModel.onPreviousStep() },
        icon = R.drawable.ico_pqrdidentificacion,
        title = "PQRDS CON IDENTIFICACIÓN",
        description = "Peticiones, quejas, reclamos y sugerencias",
        scrollContent = {
            innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(innerPadding)){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(top = 25.dp)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusManager.clearFocus() },
                ) {
                    StepIndicator(
                        currentStep = state.currentStep,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {

                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }
                        },
                        label = "StepAnimation"
                    ) { targetStep ->
                        key(targetStep) {
                            Column {
                                when (targetStep) {
                                    1 -> PqrdsIdentificationStep1Screen(
                                        navController = navController,
                                        viewModel = viewModel,
                                        codigoEntidad = codigoEntidad
                                    )

                                    2 -> PqrdsIdentificationStep2Screen(
                                        navController = navController,
                                        viewModel = viewModel,
                                        codigoEntidad = codigoEntidad
                                    )

                                    3 -> PqrdsIdentificationStep3Screen(
                                        navController = navController,
                                        viewModel = viewModel,
                                        codigoEntidad = codigoEntidad
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PqrdsIdentificationStep1Screen(
    navController: NavController,
    viewModel: PqrdsViewModel,
    codigoEntidad: String
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val errorState by viewModel.errorState.collectAsState()
    val dropDownOptionsState by viewModel.dropDownOptionsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadDropdownData(codigoEntidad)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is PqrdsEvent.NavigateNextStep -> {
                    navController.navigate(event.route)
                }
                is PqrdsEvent.OnBackStep -> {
                    navController.popBackStack()
                }
                else -> {}
            }
        }
    }

    if(dropDownOptionsState.isLoading){
        CircularProgressIndicator()
    } else {
        Text("Datos de la Secretaría",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondary)

        CustomDropdownPqrds(
            placeHolder = "*Secretaría",
            label = "Secretaría*",
            options = dropDownOptionsState.secretarias.map { it.Secretaria }, // Suponiendo que el modelo tiene 'nombre'
            selectedValue = formState.secretaria?.Secretaria ?: "",
            onValueSelected = { index ->
                viewModel.onSecretariaChange(dropDownOptionsState.secretarias[index])
            }
        )
        if (errorState.secretariaError) {
            Text(
                text = "Debe seleccionar una secretaría",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        CustomDropdownPqrds(
            placeHolder = "*Asunto de interés",
            label = "Asunto de interés*",
            options = dropDownOptionsState.asuntosInteres.map { it.Descripcion },
            selectedValue = formState.asuntoInteres?.Descripcion ?: "",
            onValueSelected = { index ->
                viewModel.onAsuntoInteresChange(dropDownOptionsState.asuntosInteres[index])
            }
        )
        if (errorState.asuntoInteresError) {
            Text(
                text = "Debe seleccionar un asunto de interés",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        CustomDropdownPqrds(
            placeHolder = "*Clasificación solicitud",
            label = "Clasificación solicitud*",
            options = dropDownOptionsState.clasificacionesSolicitud.map { it.Descripcion },
            selectedValue = formState.clasificacionSolicitud?.Descripcion ?: "",
            onValueSelected = { index ->
                viewModel.onClasificacionSolicitudChange(dropDownOptionsState.clasificacionesSolicitud[index])
            }
        )
        if (errorState.clasificacionSolicitudError) {
            Text(
                text = "Debe seleccionar una clasificación",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        CustomDropdownPqrds(
            placeHolder = "*Tipo de solicitante",
            label = "Tipo de solicitante*",
            options = dropDownOptionsState.tiposSolicitante.map { it.Descripcion },
            selectedValue = formState.tipoSolicitante?.Descripcion ?: "",
            onValueSelected = { index ->
                viewModel.onTipoSolicitanteChange(dropDownOptionsState.tiposSolicitante[index])
            }
        )
        if (errorState.tipoSolicitanteError) {
            Text(
                text = "Debe seleccionar un tipo de solicitante",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        CustomDropdownPqrds(
            placeHolder = "*Tipo atención preferencial",
            label = "Tipo atención preferencial*",
            options = dropDownOptionsState.atencionesPreferenciales.map { it.Descripcion },
            selectedValue = formState.atencionPreferencial?.Descripcion ?: "",
            onValueSelected = { index ->
                viewModel.onAtencionPreferencialChange(dropDownOptionsState.atencionesPreferenciales[index])
            }
        )
        if(errorState.atencionPreferencialError){
            Text(
                text = "Debe seleccionar una atención preferencial",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        CustomDropdownPqrds(
            placeHolder = "*Medio de respuesta",
            label = "Medio de respuesta*",
            options = dropDownOptionsState.mediosRespuesta.map { it.Descripcion },
            selectedValue = formState.medioRespuesta?.Descripcion ?: "",
            onValueSelected = { index ->
                viewModel.onMedioRespuestaChange(dropDownOptionsState.mediosRespuesta[index])
            }
        )
        if (errorState.medioRespuestaError) {
            Text(
                text = "Debe seleccionar un medio de respuesta",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
    Text("Los campos con  * son obligatorios", style = MaterialTheme.typography.bodySmall)
    Spacer(modifier = Modifier.height(20.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Gray600,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .width(150.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 18.dp)) {
            Text("Cancelar", style = MaterialTheme.typography.headlineMedium)
        }
        Button(onClick = { viewModel.onNextStep() },
            enabled = state.isNextButtonEnabled,
            modifier = Modifier
                .width(150.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 18.dp)) {
            Text("Siguiente", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
