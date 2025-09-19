package com.tramites1cero1.centralizacion.ui.screen.pqrdsidentificacion

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.ui.components.ConfirmationPoliciesDialog
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsEvent
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsResponseState
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsViewModel
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.CustomDropdownPqrds
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.ErrorDialog
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.SuccessDialog
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.Gray400
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PqrdsIdentificationStep3Screen(
    navController: NavController,
    viewModel: PqrdsViewModel,
    codigoEntidad: String
){
    val state by viewModel.uiState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val pqrdState by viewModel.pqrdState.collectAsState()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val dropDownOptionsState by viewModel.dropDownOptionsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(errorState.tipoArchivoError) {
        errorState.tipoArchivoError?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearFileTypeError() // Necesitarías crear esta función en el ViewModel
        }
    }

    if (state.showConfirmationSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideConfirmationSheet() },
            sheetState = sheetState
        ) {
            ConfirmationPoliciesDialog(
                aceptaTratamientoDatos = formState.aceptaTratamientoDatos,
                onAceptaTratamientoDatosChange = viewModel::onTratamientoDatosAcepted,
                aceptaCondicionesUso = formState.aceptaCondicionesUso,
                onAceptaCondicionesUsoChange = viewModel::onCondicionesUsoAcepted,
                onDismiss = { viewModel.hideConfirmationSheet() },
                onConfirm = {
                    // Primero oculta el sheet para que el usuario no pueda hacer doble clic,
                    // luego envía el formulario.
                    viewModel.hideConfirmationSheet()
                    viewModel.submitPqrdIdentificacion(codigoEntidad)
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDropdownData(codigoEntidad)
    }

    val outLineTextColor = TextFieldDefaults.colors(
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Gray400,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.background,
    )


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        viewModel.onFileSelected(uri)
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
        viewModel.onPreviousStep() // Dispara el evento para volver
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text("Datos Razón Social",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSecondary)

    Spacer(modifier = Modifier.height(10.dp))
    if (dropDownOptionsState.isLoading) {
        CircularProgressIndicator()
    } else {

        OutlinedTextField(
            value = formState.pais,
            onValueChange = { viewModel.setPaisName(it) },
            label = { Text("País", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        CustomDropdownPqrds(
            placeHolder = "*Departamento",
            label = "Departamento",
            options = dropDownOptionsState.departamentos.map { it.NombreDepartamento },
            selectedValue = formState.departamento?.NombreDepartamento ?: "",
            onValueSelected = { index ->
                viewModel.onDepartamentoSelected(dropDownOptionsState.departamentos[index])
            },
        )
        if (errorState.departamentoError) {
            Text("Debe seleccionar un departamento.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
        }

        Box(contentAlignment = Alignment.Center) {
            CustomDropdownPqrds(
                placeHolder = "*Ciudad",
                label = "Ciudad",
                options = dropDownOptionsState.ciudades.map { it.NombreCiudad },
                selectedValue = formState.ciudad?.NombreCiudad ?: "",
                onValueSelected = { index ->
                    viewModel.onCiudadSelected(dropDownOptionsState.ciudades[index])
                },
                // Se habilita solo cuando se ha seleccionado un departamento
                enabled = formState.departamento != null && !dropDownOptionsState.isLoadingCiudades
            )

            // Muestra un spinner mientras se cargan las ciudades
            if (dropDownOptionsState.isLoadingCiudades) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        if (errorState.ciudadError) { // <-- Añadir
            Text("Debe seleccionar una ciudad.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
        }
    }

    OutlinedTextField(
        value = formState.razonSocial,
        onValueChange = { viewModel.onRazonSocialChange(it) },
        label = { Text("Razón social*", style = MaterialTheme.typography.bodyMedium) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        colors = outLineTextColor,
        singleLine = true,
        isError = errorState.razonSocialError, // <-- Añadir
        supportingText = { // <-- Añadir
            if (errorState.razonSocialError) {
                Text("La razón social no puede estar vacía.")
            }
        }
    )
    OutlinedTextField(
        value = formState.correoElectronico,
        onValueChange = { viewModel.correoElectronicoChange(it) },
        label = { Text("Correo electrónico*", style = MaterialTheme.typography.bodyMedium) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        colors = outLineTextColor,
        singleLine = true,
        isError = errorState.correoElectronicoError, // <-- Añadir
        supportingText = { // <-- Añadir
            if (errorState.correoElectronicoError) {
                Text("Ingrese un correo electrónico válido.")
            }
        }
    )
    OutlinedTextField(
        value = formState.direccion,
        onValueChange = { viewModel.onDireccionChange(it) },
        label = { Text("Dirección", style = MaterialTheme.typography.bodyMedium) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        colors = outLineTextColor,
        singleLine = true
    )
    OutlinedTextField(
        value = formState.telefonoCelular,
        onValueChange = { viewModel.onTelefonoCelularChange(it) },
        label = { Text("Teléfono celular", style = MaterialTheme.typography.bodyMedium) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        colors = outLineTextColor,
        singleLine = true
    )
    OutlinedTextField(
        value = formState.telefonoFijo,
        onValueChange = { viewModel.onTelefonoFijoChange(it) },
        label = { Text("Telefono fijo(si tiene)", style = MaterialTheme.typography.bodyMedium) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        colors = outLineTextColor,
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = formState.descripcion,
        onValueChange = { viewModel.onDescripcionChange(it) },
        label = { Text("Descripción *", style = MaterialTheme.typography.bodyMedium) },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp),
        colors = outLineTextColor,
        singleLine = true,
        isError = errorState.descripcionError,
        supportingText = {
            if (errorState.descripcionError) {
                Text("La descripción no puede estar vacía.")
            }
        }
    )
    Spacer(modifier = Modifier.height(16.dp))

    if(formState.nombreArchivo == null || formState.nombreArchivo == ""){
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Button(onClick = { filePickerLauncher.launch("*/*") }) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text("Adjuntar documentos", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable { filePickerLauncher.launch("*/*") }
            .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text("Seleccionar documentos", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Gray300, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Archivo adjuntado",
                tint = Green
            )
            Text(
                text = formState.nombreArchivo ?: "",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            // Opcional: Botón para quitar el archivo
            IconButton(onClick = { viewModel.onFileSelected(null) }) {
                Icon(Icons.Default.Cancel, contentDescription = "Quitar archivo")
            }
        }
    }
    Spacer(modifier = Modifier.height(5.dp))
    Text("Los campos con  * son obligatorios", style = MaterialTheme.typography.bodySmall)
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 20.dp),
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
        Button(
            // El botón se deshabilita mientras está cargando
            enabled = pqrdState != PqrdsResponseState.LoadingPqrd,
            onClick = { viewModel.onFinalizeClicked() },
            modifier = Modifier
                .width(150.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 18.dp)
        ) {
            Text("Finalizar", style = MaterialTheme.typography.headlineMedium)
        }
    }

    when (val state = pqrdState) {
        is PqrdsResponseState.LoadingPqrd -> {
            // Muestra un indicador de carga en el centro de la pantalla
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false, onClick = {}) // Bloquea clics detrás
            ) {
                CircularProgressIndicator()
            }
        }
        is PqrdsResponseState.Success -> {
            // Muestra un diálogo de éxito
            SuccessDialog(
                ticket = state.token,
                onDismiss = {
                    viewModel.resetSubmissionState()
                    // Navega a la pantalla de inicio o a donde necesites
                    navController.popBackStack("pqrd_choice", inclusive = false)
                }
            )
        }
        is PqrdsResponseState.ErrorPqrd -> {
            // Muestra un diálogo de error
            ErrorDialog(
                message = state.message,
                onDismiss = {
                    viewModel.resetSubmissionState()
                }
            )
        }
        is PqrdsResponseState.Empty -> {
            // No hace nada, la UI está en su estado normal
        }
    }


}