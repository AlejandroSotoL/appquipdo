package com.tramites1cero1.centralizacion.ui.screen.pqrdsidentificacion

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsEvent
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsViewModel
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.CustomDropdownPqrds
import com.tramites1cero1.centralizacion.ui.theme.Gray400
import com.tramites1cero1.centralizacion.ui.theme.Gray600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PqrdsIdentificationStep2Screen(
    navController: NavController,
    viewModel: PqrdsViewModel,
    codigoEntidad: String
){
    val state by viewModel.uiState.collectAsState()
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

    val outLineTextColor = TextFieldDefaults.colors(
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Gray400,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.background,
    )

    BackHandler(enabled = true) {
        viewModel.onPreviousStep() // Dispara el evento para volver
    }

        Spacer(modifier = Modifier.height(16.dp))
        if(dropDownOptionsState.isLoading){
            CircularProgressIndicator()
        } else {
            Text("Datos del ciudadano o contribuyente",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondary)

            CustomDropdownPqrds(
                placeHolder = "*Tipo de documento",
                label = "Tipo de documento*",
                options = dropDownOptionsState.tiposDocumento.map { it.Descripcion }, // Suponiendo que el modelo tiene 'nombre'
                selectedValue = formState.tipoDocumento?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onTipoDocumentoChange(dropDownOptionsState.tiposDocumento[index])
                }
            )
            if (errorState.tipoDocumentoError) {
                Text(
                    text = "Seleccione un tipo de documento",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            OutlinedTextField(
                value = formState.identificacion,
                onValueChange = { viewModel.onIdentificacionChange(it) },
                label = { Text("Identificación*", style = MaterialTheme.typography.bodyMedium) },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outLineTextColor,
                singleLine = true,
                isError = errorState.identificacionError,
                supportingText = {
                    if (errorState.identificacionError) {
                        Text("Por favor, introduce un documento.")
                    }
                }
            )


            OutlinedTextField(
                value = formState.primerNombre,
                onValueChange = { viewModel.onPrimerNombreChange(it) },
                label = { Text("Primer nombre*", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(10.dp),
                colors = outLineTextColor,
                singleLine = true,
                isError = errorState.primerNombreError,
                supportingText = {
                    if (errorState.primerNombreError) {
                        Text("El primer nombre es requerido.")
                    }
                }
            )
            OutlinedTextField(
                value = formState.segundoNombre,
                onValueChange = { viewModel.onSegundoNombreChange(it) },
                label = { Text("Segundo nombre", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(10.dp),
                colors = outLineTextColor,
                singleLine = true
            )
            OutlinedTextField(
                value = formState.primerApellido,
                onValueChange = { viewModel.onPrimerApellidoChange(it) },
                label = { Text("Primer apellido*", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(10.dp),
                colors = outLineTextColor,
                singleLine = true,
                isError = errorState.primerApellidoError,
                supportingText = {
                    if (errorState.primerApellidoError) {
                        Text("El primer apellido es requerido.")
                    }
                }
            )
            OutlinedTextField(
                value = formState.segundoApellido,
                onValueChange = { viewModel.onSegundoApellidoChange(it) },
                label = { Text("Segundo apellido", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(10.dp),
                colors = outLineTextColor,
                singleLine = true
            )

            CustomDropdownPqrds(
                placeHolder = "*grupo de interés",
                label = "¿Pertenece a algún grupo de interés?",
                options = dropDownOptionsState.gruposInteres.map { it.Descripcion },
                selectedValue = formState.grupoInteres?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onGrupoInteresChange(dropDownOptionsState.gruposInteres[index])
                }
            )
            if(errorState.grupoInteresError){
                Text(
                    text = "Debe seleccionar un grupo de interés",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "condición de discapacidad",
                label = "¿Tiene condición de discapacidad?",
                options = dropDownOptionsState.discapacidades.map { it.Descripcion },
                selectedValue = formState.discapacidad?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onDiscapacidadChange(dropDownOptionsState.discapacidades[index])
                }
            )
            if(errorState.discapacidadError){
                Text(
                    text = "Debe seleccionar una condición de discapacidad válida",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*grupo étnico",
                label = "¿Pertenece a algún grupo étnico?",
                options = dropDownOptionsState.gruposEtnicos.map { it.Descripcion },
                selectedValue = formState.grupoEtnico?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onGrupoEtnicoChange(dropDownOptionsState.gruposEtnicos[index])
                }
            )
            if(errorState.grupoEtnicoError){
                Text(
                    text = "Debe seleccionar un grupo étnico",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*Género",
                label = "Género",
                options = dropDownOptionsState.generos.map { it.Descripcion },
                selectedValue = formState.genero?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onGeneroChange(dropDownOptionsState.generos[index])
                }
            )
            if(errorState.generoError){
                Text(
                    text = "Debe seleccionar un género",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*Edad",
                label = "Rango de edad",
                options = dropDownOptionsState.rangosEdad.map { it.Descripcion },
                selectedValue = formState.rangoEdad?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onRangoEdadChange(dropDownOptionsState.rangosEdad[index])
                }
            )
            if(errorState.rangoEdadError){
                Text(
                    text = "Debe seleccionar un rango de edad",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*Actividad económica",
                label = "Actividad económica",
                options = dropDownOptionsState.actividadesEconomicas.map { it.Descripcion },
                selectedValue = formState.actividadEconomica?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onActividadEconomicaChange(dropDownOptionsState.actividadesEconomicas[index])
                }
            )
            if(errorState.actividadEconomicaError){
                Text(
                    text = "Debe seleccionar una actividad económica",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*Estrato",
                label = "Estrato",
                options = dropDownOptionsState.nivelesEstrato.map { it.Descripcion },
                selectedValue = formState.nivelEstrato?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onNivelEstratoChange(dropDownOptionsState.nivelesEstrato[index])
                }
            )
            if(errorState.nivelEstratoError){
                Text(
                    text = "Por favor, seleccione un nivel de estrato",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*Nivel sisbén",
                label = "Nivel sisbén",
                options = dropDownOptionsState.nivelesSisben.map { it.Descripcion },
                selectedValue = formState.nivelSisben?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onNivelSisbenChange(dropDownOptionsState.nivelesSisben[index])
                }
            )
            if(errorState.nivelSisbenError){
                Text(
                    text = "Por favor, seleccione un nivel de Sisbén",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            CustomDropdownPqrds(
                placeHolder = "*Escolaridad",
                label = "Escolaridad",
                options = dropDownOptionsState.escolaridades.map { it.Descripcion },
                selectedValue = formState.escolaridad?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onEscolaridadChange(dropDownOptionsState.escolaridades[index])
                }
            )
            CustomDropdownPqrds(
                placeHolder = "*Vulnerabilidad",
                label = "Vulnerabilidad",
                options = dropDownOptionsState.vulnerabilidades.map { it.Descripcion },
                selectedValue = formState.vulnerabilidad?.Descripcion ?: "",
                onValueSelected = { index ->
                    viewModel.onVulnerabilidadChange(dropDownOptionsState.vulnerabilidades[index])
                }
            )
            Text("Los campos con  * son obligatorios", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { viewModel.onPreviousStep() },
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